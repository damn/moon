(ns moon.application
  (:require [clojure.disposable :as disposable]
            [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.graphics.viewport :as viewport]
            [qrecord.core :as q]
            [moon.malli :as m])
  (:import (com.badlogic.gdx ApplicationListener)))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some]
    [:ctx/schema :some]
    [:ctx/active-entities :any]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/colors :some]
    [:ctx/content-grid :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/cursors :some]
    [:ctx/db :some]
    [:ctx/default-font :some]
    [:ctx/delta-time :any]
    [:ctx/elapsed-time :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/files :some]
    [:ctx/graphics :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/input :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/mouseover-eid :any]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/render-z-order :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/start-position :some]
    [:ctx/textures :some]
    [:ctx/tiled-map :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/ui-viewport :some]
    [:ctx/unit-scale :some]
    [:ctx/world-mouse-position :any]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/z-orders :some]
    ]))

(q/defrecord Context [])

(defn- create!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (merge (map->Context {})
                 (let [batch (sprite-batch/create)]
                   {
                    :ctx/schema schema
                    :ctx/app      (gdx/app)
                    :ctx/audio    (gdx/audio)
                    :ctx/graphics (gdx/graphics)
                    :ctx/files    (gdx/files)
                    :ctx/input    (gdx/input)
                    :ctx/batch batch

                    :ctx/active-entities nil
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
                    }))
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
