(ns game.listener.render.draw-world.draw-tile-grid
  (:import (com.badlogic.gdx.graphics Color OrthographicCamera)
           (com.badlogic.gdx.math Vector3)
           (com.badlogic.gdx.utils.viewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn- draw-fn-line [shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.line ^ShapeDrawer shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draw-fn-line shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draw-fn-line shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn draw-tile-grid
  [ctx shape-drawer ^Viewport world-viewport show-tile-grid?]
  (when @show-tile-grid?
    (let [^OrthographicCamera camera (.getCamera world-viewport)
          plane-points (mapv (fn [^Vector3 v3]
                               [(.x v3) (.y v3) (.z v3)])
                             (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))]
      (draw-fn-grid shape-drawer
                    (int left-x)
                    (int bottom-y)
                    (inc (int (.getWorldWidth world-viewport)))
                    (+ 2 (int (.getWorldHeight world-viewport)))
                    1
                    1
                    (Color/toFloatBits (float 1) (float 1) (float 1) (float 0.8))))))
