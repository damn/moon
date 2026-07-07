(ns clojure.editor-start
  (:require [clojure.add-component-window :as add-component-window]
            [clojure.add-stage-actors :as add-stage-actors]
            [clojure.application :as application]
            [clojure.assoc-create :as assoc-create]
            [clojure.clear-screen :as clear-screen]
            [clojure.component-row :as component-row]
            [clojure.create-widget]
            [clojure.ctx-batch :as ctx-batch]
            [clojure.ctx-db :as ctx-db]
            [clojure.ctx-skin :as ctx-skin]
            [clojure.ctx-stage :as ctx-stage]
            [clojure.ctx-textures :as ctx-textures]
            [clojure.disposable :as disposable]
            [clojure.do :as do]
            [clojure.editor-rebuild :as editor-rebuild]
            [clojure.gdx-context :as gdx-context]
            [clojure.listener :as listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.main-window :as main-window]
            [clojure.map-create :as map-create]
            [clojure.map-widget-table :as map-widget-table]
            [clojure.property-overview-window :as property-overview-window]
            [clojure.set-ctx :as set-ctx]
            [clojure.stage :as stage]
            [clojure.viewport :as viewport]
            [clojure.widget-value]))

(defn- create-app! [ctx data]
  (merge ctx data))

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
     :create-pipeline [[gdx-context/f]
                       [assoc-create/f :ctx/batch ctx-batch/step]
                       [assoc-create/f :ctx/skin ctx-skin/step]
                       [assoc-create/f :ctx/db ctx-db/step]
                       [assoc-create/f :ctx/stage ctx-stage/step]
                       [do/step
                        [add-stage-actors/f! [[main-window/create]]]]
                       [map-create/f :ctx/textures [ctx-textures/step
                                                    {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}]]
                       [create-app!
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
                                                     :skill/cost]}]]
     :dispose! dispose-app!
     :render-pipeline [[clear-screen/step]
                       [render-app!]]
     :resize! resize-app!})))
