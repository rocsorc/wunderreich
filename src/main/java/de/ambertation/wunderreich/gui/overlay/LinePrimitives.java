package de.ambertation.wunderreich.gui.overlay;

import de.ambertation.wunderlib.math.Bounds;
import de.ambertation.wunderlib.math.Float2;
import de.ambertation.wunderlib.math.Float3;
import de.ambertation.wunderlib.math.Transform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class LinePrimitives {

    //-------------------------------------- VERTEX HANDLING --------------------------------------
    private static void addVertex(
            RenderContext ctx,
            Float3 p,
            float nx, float ny, float nz,
            int color
    ) {
        ctx.vertexConsumer.addVertex(ctx.pose(), (float) p.x, (float) p.y, (float) p.z)
                          .setColor(color)
                          .setNormal(ctx.normal(), nx, ny, nz);
    }

    private static void addVertex(
            RenderContext ctx,
            int corner,
            float nx, float ny, float nz,
            int color,
            Float3[] corners
    ) {
        addVertex(ctx, corners[corner], nx, ny, nz, color);
    }


    //-------------------------------------- LINE --------------------------------------
    public static void renderLine(
            RenderContext ctx,
            Float3 start, Float3 end,
            int color, float alpha
    ) {
        addLine(ctx, start.add(ctx.worldToCamSpace), end.add(ctx.worldToCamSpace),
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    private static void addLine(
            RenderContext ctx,
            Bounds.Interpolate cornerStart, Bounds.Interpolate cornerEnd,
            int color, Float3[] corners
    ) {
        Float3 start = corners[cornerStart.idx];
        Float3 end = corners[cornerEnd.idx];
        Float3 n = end.sub(start).normalized();

        addVertex(ctx, start, (float) n.x, (float) n.y, (float) n.z, color);
        addVertex(ctx, end, (float) n.x, (float) n.y, (float) n.z, color);
    }


    private static void addLine(
            RenderContext ctx,
            Float3 start, Float3 end,
            int color
    ) {
        Float3 n = end.sub(start).normalized();

        addVertex(ctx, start, (float) n.x, (float) n.y, (float) n.z, color);
        addVertex(ctx, end, (float) n.x, (float) n.y, (float) n.z, color);
    }

    //-------------------------------------- 2D Shapes --------------------------------------
    public static void renderQuad(
            RenderContext ctx,
            Float3 p1, Float3 p2, Float3 p3, Float3 p4,
            int color, float alpha
    ) {
        renderQuad(ctx, p1, p2, p3, p4,
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    public static void renderQuadXZ(
            RenderContext ctx,
            Float3 center, Float2 size,
            int color, float alpha
    ) {
        Float3 sz = size.div(2).xxy();
        renderQuad(
                ctx,
                center.add(sz.mul(Float3.XZ_PLANE)),
                center.add(sz.mul(Float3.XmZ_PLANE)),
                center.add(sz.mul(Float3.mXmZ_PLANE)),
                center.add(sz.mul(Float3.mXZ_PLANE)),
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    public static void renderQuad(
            RenderContext ctx,
            Float3 p1, Float3 p2, Float3 p3, Float3 p4,
            int color
    ) {
        renderQuadCameraSpace(
                ctx,
                p1.add(ctx.worldToCamSpace),
                p2.add(ctx.worldToCamSpace),
                p3.add(ctx.worldToCamSpace),
                p4.add(ctx.worldToCamSpace),
                color
        );
    }

    private static void renderQuadCameraSpace(
            RenderContext ctx,
            Float3 p1, Float3 p2, Float3 p3, Float3 p4,
            int color
    ) {
        addLine(ctx, p1, p2, color);
        addLine(ctx, p2, p3, color);
        addLine(ctx, p3, p4, color);
        addLine(ctx, p4, p1, color);
    }

    //-------------------------------------- SINGLE BLOCKS --------------------------------------
    public static void renderSingleBlock(
            RenderContext ctx,
            BlockPos pos,
            float deflate,
            int color,
            float alpha
    ) {
        final float x = (float) (pos.getX() + ctx.worldToCamSpace.x);
        final float y = (float) (pos.getY() + ctx.worldToCamSpace.y);
        final float z = (float) (pos.getZ() + ctx.worldToCamSpace.z);
        renderCubeOutlineCameraSpace(ctx,
                x + deflate, y + deflate, z + deflate,
                1 + x - deflate, 1 + y - deflate, 1 + z - deflate,
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    public static void renderSingleBlock(RenderContext ctx, Float3 pos, float deflate, int color, float alpha) {
        final float x = (float) (pos.x + ctx.worldToCamSpace.x) - 0.5f;
        final float y = (float) (pos.y + ctx.worldToCamSpace.y) - 0.5f;
        final float z = (float) (pos.z + ctx.worldToCamSpace.z) - 0.5f;
        renderCubeOutlineCameraSpace(ctx,
                x + deflate, y + deflate, z + deflate,
                1 + x - deflate, 1 + y - deflate, 1 + z - deflate,
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    //-------------------------------------- BoundingBox --------------------------------------
    public static void renderBounds(RenderContext ctx, Bounds bounds, float deflate, int color, float alpha) {
        renderCubeOutlineCameraSpace(
                ctx,
                (float) (bounds.min.x + ctx.worldToCamSpace.x) + deflate - 0.5f,
                (float) (bounds.min.y + ctx.worldToCamSpace.y) + deflate - 0.5f,
                (float) (bounds.min.z + ctx.worldToCamSpace.z) + deflate - 0.5f,
                (float) (bounds.max.x + ctx.worldToCamSpace.x) - deflate + 0.5f,
                (float) (bounds.max.y + ctx.worldToCamSpace.y) - deflate + 0.5f,
                (float) (bounds.max.z + ctx.worldToCamSpace.z) - deflate + 0.5f,
                FastColor.ARGB32.color(color, (int) (alpha * 0xFF))
        );
    }

    private static void renderCubeOutlineCameraSpace(
            RenderContext ctx,
            float lx, float ly, float lz,
            float hx, float hy, float hz,
            int color
    ) {
        final Matrix4f pose = ctx.pose();
        final PoseStack.Pose normal = ctx.normal();

        ctx.vertexConsumer.addVertex(pose, lx, ly, lz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, lz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, ly, lz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, lz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, ly, lz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
        ctx.vertexConsumer.addVertex(pose, lx, ly, hz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, lz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, lz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, lz).setColor(color).setNormal(normal, -1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, lz).setColor(color).setNormal(normal, -1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, lz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, hz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, hz).setColor(color).setNormal(normal, 0.0F, -1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, ly, hz).setColor(color).setNormal(normal, 0.0F, -1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, lx, ly, hz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, hz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, hz).setColor(color).setNormal(normal, 0.0F, 0.0F, -1.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, lz).setColor(color).setNormal(normal, 0.0F, 0.0F, -1.0F);
        ctx.vertexConsumer.addVertex(pose, lx, hy, hz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, hz).setColor(color).setNormal(normal, 1.0F, 0.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, ly, hz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, hz).setColor(color).setNormal(normal, 0.0F, 1.0F, 0.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, lz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
        ctx.vertexConsumer.addVertex(pose, hx, hy, hz).setColor(color).setNormal(normal, 0.0F, 0.0F, 1.0F);
    }


    //-------------------------------------- TRANSFORMS --------------------------------------
    public static void renderTransform(RenderContext ctx, Transform t, int color, float alpha) {
        renderTransform(ctx, t, FastColor.ARGB32.color(color, (int) (alpha * 0xFF)));
    }

    private static void renderTransform(RenderContext ctx, Transform t, int color) {
        Float3[] corners = t.translate(ctx.worldToCamSpace).getCornersInWorldSpace(false);

        ctx.pushText(
                t.toString(),
                corners[0].x - ctx.worldToCamSpace.x,
                corners[0].y - 0.1 - ctx.worldToCamSpace.y,
                corners[0].z - ctx.worldToCamSpace.z,
                OverlayRenderer.COLOR_BOUNDING_BOX
        );

        renderCornersInCamSpace(ctx, corners, color);
    }

    public static void renderCorners(RenderContext ctx, Float3[] corners, int color, float alpha) {
        Float3[] camCorners = new Float3[corners.length];
        for (int i = 0; i < camCorners.length; i++) {
            camCorners[i] = corners[i].add(ctx.worldToCamSpace);
        }
        renderCornersInCamSpace(ctx, camCorners, FastColor.ARGB32.color(color, (int) (alpha * 0xFF)));
    }

    public static void renderCornersInCamSpace(RenderContext ctx, Float3[] corners, int color) {
        addLine(ctx,
                Bounds.Interpolate.MIN_MIN_MIN,
                Bounds.Interpolate.MAX_MIN_MIN,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MIN_MIN,
                Bounds.Interpolate.MAX_MIN_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MIN_MAX,
                Bounds.Interpolate.MIN_MIN_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MIN_MIN_MAX,
                Bounds.Interpolate.MIN_MIN_MIN,
                color, corners
        );


        addLine(ctx,
                Bounds.Interpolate.MIN_MAX_MIN,
                Bounds.Interpolate.MAX_MAX_MIN,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MAX_MIN,
                Bounds.Interpolate.MAX_MAX_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MAX_MAX,
                Bounds.Interpolate.MIN_MAX_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MIN_MAX_MAX,
                Bounds.Interpolate.MIN_MAX_MIN,
                color, corners
        );


        addLine(ctx,
                Bounds.Interpolate.MIN_MIN_MIN,
                Bounds.Interpolate.MIN_MAX_MIN,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MIN_MIN_MAX,
                Bounds.Interpolate.MIN_MAX_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MIN_MAX,
                Bounds.Interpolate.MAX_MAX_MAX,
                color, corners
        );
        addLine(ctx,
                Bounds.Interpolate.MAX_MIN_MIN,
                Bounds.Interpolate.MAX_MAX_MIN,
                color, corners
        );
    }
}
