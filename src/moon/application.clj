(ns moon.application
  (:require [clojure.disposable :as disposable]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors]
            [clojure.graphics.viewport :as viewport]
            )
  (:import (com.badlogic.gdx ApplicationListener)))

(defn- create!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {:ctx/active-entities nil
           :ctx/delta-time nil
           :ctx/mouseover-eid nil
           :ctx/ui-mouse-position nil
           :ctx/world-mouse-position nil
           :ctx/elapsed-time 0
           :ctx/paused? false
           :ctx/unit-scale (atom 1)
           :ctx/factions-iterations {:good 15 :evil 5}
           :ctx/max-delta 0.04
           :ctx/minimum-size 0.39
           :ctx/z-orders [:z-order/on-ground
                          :z-order/ground
                          :z-order/flying
                          :z-order/effect]
           }
          create-fns))

(defn-  dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose! (vals audio))
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map)
  nil)

(defn- render! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))

(defn- resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)

(def state (atom nil))

(defn start! [{:keys [colors listener config]}]
  (colors/put! colors)
  (config/use-glfw-async!)
  (lwjgl/application! (let [{:keys [create-params
                                    render-params]} listener]
                        (reify ApplicationListener
                          (create [_]
                            (reset! state (create! create-params)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state render! render-params))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_])))
                      (config/create config)))
