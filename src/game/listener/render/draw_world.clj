(ns game.listener.render.draw-world
  (:require [game.listener.render.draw-world.draw-cell-debug :refer [draw-cell-debug]]
            [game.listener.render.draw-world.draw-entities :refer [draw-entities!]]
            [game.listener.render.draw-world.draw-tile-grid :refer [draw-tile-grid]]
            [game.listener.render.draw-world.highlight-mouseover-tile :refer [highlight-mouseover-tile]]
            [game.mouse :refer [mouseover-actor
                                ui-mouse-position
                                world-mouse-position]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.utils.viewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn draw-world!
  [batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
   world player-eid raycaster elapsed-time show-body-bounds? active-entities
   show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations render-z-order]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (.setColor ^Batch batch (float 1) (float 1) (float 1) (float 1))
    (.setProjectionMatrix ^Batch batch (.combined camera))
    (.begin ^Batch batch)
    (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* world-unit-scale old-line-width))
      (reset! unit-scale world-unit-scale)
      (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport show-tile-grid?)
                       #(draw-cell-debug % shape-drawer world-viewport world
                                         show-cell-entities? show-cell-occupied? show-potential-field-colors?
                                         factions-iterations)
                       #(draw-entities! % shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor* world-mouse-pos
                                        player-eid raycaster elapsed-time show-body-bounds? active-entities render-z-order)
                       #(highlight-mouseover-tile % shape-drawer world-mouse-pos world)]]
        (draw-fn nil))
      (reset! unit-scale 1)
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width))
    (.end ^Batch batch)))
