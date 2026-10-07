(ns game.listener.create.ui-actors.player-state-draw
  (:require [game.draw :refer [draw-fn-texture-region]]
            [game.mouse :refer [mouseover-actor ui-mouse-position]]
            [moon.textures :as textures]))

(defn- entity-state-draw-ui-view
  [[k _v] eid batch unit-scale textures mouseover-actor ui-mouse-position]
  (case k
    :player-item-on-cursor
    (when mouseover-actor
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-position
                              {:center? true}))

    nil))

(defn player-state-draw-create [unit-scale textures player-eid]
  (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
    (act [delta]
      (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
            player-eid @player-eid
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            mouse-pos (ui-mouse-position stage)
            [x y] mouse-pos]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   batch
                                   unit-scale
                                   textures
                                   (mouseover-actor stage x y)
                                   mouse-pos)))))
