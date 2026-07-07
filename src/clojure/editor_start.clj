(ns clojure.editor-start
  (:require [clojure.add-component-window :as add-component-window]
            [clojure.application :as application]
            [clojure.clear-screen :as clear-screen]
            [clojure.component-row :as component-row]
            [clojure.create-widget]
            [clojure.ctx-batch :as ctx-batch]
            [clojure.ctx-db :as ctx-db]
            [clojure.ctx-skin :as ctx-skin]
            [clojure.ctx-stage :as ctx-stage]
            [clojure.ctx-textures :as ctx-textures]
            [clojure.disposable :as disposable]
            [clojure.editor-rebuild :as editor-rebuild]
            [clojure.gdx.audio :as audio]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.input :as input]
            [clojure.listener :as listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.main-window :as main-window]
            [clojure.map-widget-table :as map-widget-table]
            [clojure.property-overview-window :as property-overview-window]
            [clojure.set-ctx :as set-ctx]
            [clojure.stage :as stage]
            [clojure.viewport :as viewport]
            [clojure.widget-value]))

(defn- create [_ctx]
  (let [ctx {:ctx/audio (audio/f)
             :ctx/files (files/f)
             :ctx/graphics (graphics/f)
             :ctx/input (input/f)}
        ctx (assoc ctx :ctx/batch (ctx-batch/step ctx))
        ctx (assoc ctx :ctx/skin (ctx-skin/step ctx))
        ctx (assoc ctx :ctx/db (ctx-db/step ctx))
        ctx (assoc ctx :ctx/stage (ctx-stage/step ctx))
        _ (stage/add-actor! (:ctx/stage ctx) (main-window/create ctx))
        ctx (assoc ctx :ctx/textures (ctx-textures/step ctx {:folder "resources/"
                                                             :extensions #{"png" "bmp"}}))]
    (merge ctx
           {:ctx/property-overview-window property-overview-window/create
            :ctx/add-component-window add-component-window/f
            :ctx/create-component-row component-row/create
            :ctx/create-map-widget-table map-widget-table/create
            :ctx/rebuild-editor-window! editor-rebuild/f!
            :ctx/property-k-sort-order [:property/id
                                        :property/pretty-name
                                        :entity/image
                                        :entity/animation
                                        :entity/species
                                        :creature/level
                                        :entity/body
                                        :item/slot
                                        :projectile/speed
                                        :projectile/max-range
                                        :projectile/piercing?
                                        :skill/action-time-modifier-key
                                        :skill/action-time
                                        :skill/start-action-sound
                                        :skill/cost]})))

(defn- dispose-app! [{:keys [ctx/skin
                             ctx/batch
                             ctx/textures]}]
  (disposable/dispose! batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures)))

(defn- render-app! [{:keys [ctx/stage]
                     :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn- resize-app! [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))

(defn -main []
  (lwjgl3-application/f!
   {:title "!Editor!"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}
   (listener/f
    {:state-var #'application/state
     :create-pipeline [[create]]
     :dispose! dispose-app!
     :render-pipeline [[clear-screen/step]
                       [render-app!]]
     :resize! resize-app!})))
