(ns moon.game
  (:require [clojure.disposable :as disposable]
            [clojure.graphics.viewport :as viewport]))

(defn create!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {}
          create-fns))

(defn dispose!
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

(defn render! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)
