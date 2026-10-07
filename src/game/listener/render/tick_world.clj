(ns game.listener.render.tick-world
  (:require [game.listener.render.tick-world.tick-component :refer [tick-component]]
            [effect.applicable :refer [applicable?]]
            [game.shared :refer [colors
                                 handle-effect
                                 world-mouse-position]]
            [moon.world :as world])
  (:import (com.badlogic.gdx Gdx Graphics)))

(defn- tick-entities!
  [db world raycaster elapsed-time delta-time active-entities
   audio skin stage textures z-orders minimum-size max-speed world-viewport]
  (let [active-entities* @active-entities
        raycaster* @raycaster
        world-mouse-pos (world-mouse-position world-viewport)]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [effect (filter #(applicable? % effect-ctx) effects)]
                (handle-effect db world elapsed-time audio skin stage textures z-orders minimum-size
                               effect effect-ctx world-mouse-pos
                               apply-effects!
                               active-entities*
                               colors
                               raycaster*)))]
      (doseq [eid active-entities*
              component @eid]
        (tick-component world raycaster elapsed-time delta-time audio skin stage textures z-orders
                        minimum-size max-speed
                        nil world-mouse-pos
                        apply-effects!
                        eid component)))))

(defn tick-game!
  [db world raycaster elapsed-time delta-time potential-field-cache active-entities paused?
   audio skin stage textures world-viewport factions-iterations z-orders
   minimum-size max-delta max-speed]
  (when-not @paused?
    (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
      (reset! delta-time delta-ms)
      (swap! elapsed-time + delta-ms))
    (doseq [[faction max-iterations] factions-iterations]
      (world/update-potential-fields! @world
                                      potential-field-cache
                                      faction
                                      @active-entities
                                      max-iterations))
    (tick-entities! db world raycaster elapsed-time delta-time active-entities
                    audio skin stage textures z-orders minimum-size max-speed world-viewport)))
