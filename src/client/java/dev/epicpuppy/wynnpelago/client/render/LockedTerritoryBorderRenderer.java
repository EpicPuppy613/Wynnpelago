package dev.epicpuppy.wynnpelago.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wynntils.core.components.Models;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.mc.type.PoiLocation;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class LockedTerritoryBorderRenderer {
    // Ref: https://docs.fabricmc.net/1.21.11/develop/rendering/world

    private static final RenderPipeline LOCKED_TERRITORY_OUTLINES = RenderPipelines.register(RenderPipeline.builder(
                    RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Wynnpelago.MOD_ID, "pipeline/locked_territory_outlines"))
            .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build());

    private static final double RENDER_DISTANCE = 192;
    private static final int VERTICAL_DESCENT = 32;
    private static final int VERTICAL_ASCENT = 64;

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();

    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);

    public static boolean enableRender = true;

    @Getter
    private static LockedTerritoryBorderRenderer instance;

    private BufferBuilder buffer;
    private MappableRingBuffer vertexBuffer;

    public LockedTerritoryBorderRenderer() {
        instance = this;
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(this::extractAndDrawOutlines);
    }

    private void extractAndDrawOutlines(WorldRenderContext context) {
        if (!WynnpelagoClient.enabled || !enableRender) return;
        if (render(context)) {
            draw(Minecraft.getInstance(), LOCKED_TERRITORY_OUTLINES);
        }
        ;
    }

    private boolean render(WorldRenderContext context) {
        PoseStack matrices = context.matrices();
        Vec3 camera = context.worldState().cameraRenderState.pos;
        if (Minecraft.getInstance().player == null) return false;
        Vec3 playerPos = Minecraft.getInstance().player.position();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (buffer == null) {
            buffer = new BufferBuilder(
                    allocator,
                    LOCKED_TERRITORY_OUTLINES.getVertexFormatMode(),
                    LOCKED_TERRITORY_OUTLINES.getVertexFormat());
        }

        boolean rendered = false;
        Set<TerritoryPoi> territories = Models.Territory.getTerritoryPois();
        for (TerritoryPoi territory : territories) {
            if (TerritoryUnlock.unlockedTerritories.contains(territory.getName())) continue;
            PoiLocation loc = territory.getLocation();
            if (playerPos.distanceTo(new Vec3(loc.getX(), playerPos.y, loc.getZ())) > RENDER_DISTANCE) continue;
            TerritoryProfile territoryProfile = territory.getTerritoryProfile();
            renderTerritoryOutline(
                    matrices.last(),
                    buffer,
                    (float) playerPos.x,
                    (float) playerPos.z,
                    (float) playerPos.y,
                    territoryProfile.getStartX() + 0.01f,
                    territoryProfile.getEndX() - 0.01f,
                    territoryProfile.getStartZ() + 0.01f,
                    territoryProfile.getEndZ() - 0.01f);
            rendered = true;
        }

        matrices.popPose();

        return rendered;
    }

    private void renderTerritoryOutline(
            PoseStack.Pose pose,
            BufferBuilder buffer,
            float pX,
            float pZ,
            float y,
            float x1,
            float x2,
            float z1,
            float z2) {
        Matrix4fc matrix = pose.pose();

        boolean inTerritory = pX >= x1 && pZ >= z1 && pX <= x2 && pZ <= z2;

        if (inTerritory || pX <= x1) {
            buffer.addVertex(matrix, x1, y - VERTICAL_DESCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y - VERTICAL_DESCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y + VERTICAL_ASCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y + VERTICAL_ASCENT, z1).setColor(255, 0, 0, 63);
        }

        if (inTerritory || pZ >= z2) {
            buffer.addVertex(matrix, x1, y - VERTICAL_DESCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y - VERTICAL_DESCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y + VERTICAL_ASCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y + VERTICAL_ASCENT, z2).setColor(255, 0, 0, 63);
        }

        if (inTerritory || pX >= x2) {
            buffer.addVertex(matrix, x2, y - VERTICAL_DESCENT, z2).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y - VERTICAL_DESCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y + VERTICAL_ASCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y + VERTICAL_ASCENT, z2).setColor(255, 0, 0, 63);
        }

        if (inTerritory || pZ <= z1) {
            buffer.addVertex(matrix, x2, y - VERTICAL_DESCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y - VERTICAL_DESCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x1, y + VERTICAL_ASCENT, z1).setColor(255, 0, 0, 63);
            buffer.addVertex(matrix, x2, y + VERTICAL_ASCENT, z1).setColor(255, 0, 0, 63);
        }
    }

    private void draw(Minecraft client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline) {
        // Build the buffer
        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);

        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);

        // Rotate the vertex buffer so we are less likely to use buffers that the GPU is using
        vertexBuffer.rotate();
        buffer = null;
    }

    private GpuBuffer upload(MeshData.DrawState drawParameters, VertexFormat format, MeshData builtBuffer) {
        // Calculate the size needed for the vertex buffer
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        // Initialize or resize the vertex buffer as needed
        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            if (vertexBuffer != null) {
                vertexBuffer.close();
            }

            vertexBuffer = new MappableRingBuffer(
                    () -> Wynnpelago.MOD_ID + " locked territory outlines",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE,
                    vertexBufferSize);
        }

        // Copy vertex data into the vertex buffer
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(
                vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data());
        }

        return vertexBuffer.currentBuffer();
    }

    private static void draw(
            Minecraft client,
            RenderPipeline pipeline,
            MeshData builtBuffer,
            MeshData.DrawState drawParameters,
            GpuBuffer vertices,
            VertexFormat format) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.Mode.QUADS) {
            // Sort the quads if there is translucency
            builtBuffer.sortQuads(allocator, RenderSystem.getProjectionType().vertexSorting());
            // Upload the index buffer
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());
            indexType = builtBuffer.drawState().indexType();
        } else {
            // Use the general shape index buffer for non-quad draw modes
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer =
                    RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        // Actually execute the draw
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> Wynnpelago.MOD_ID + " locked territory outline rendering",
                        client.getMainRenderTarget().getColorTextureView(),
                        OptionalInt.empty(),
                        client.getMainRenderTarget().getDepthTextureView(),
                        OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);

            //noinspection ConstantValue
            renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1);
        }

        builtBuffer.close();
    }

    public void close() {
        allocator.close();

        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }
}
