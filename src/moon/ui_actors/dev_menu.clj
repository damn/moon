(ns moon.ui-actors.dev-menu
  (:require [clojure.string :as str]
            [moon.db :as db]
            [moon.graphics.camera :as camera]
            [moon.input :as input]
            [moon.ui.data-viewer-window :as data-viewer-window]
            [moon.ui.dev-menu :as dev-menu]
            [moon.ui.property-overview-window :as property-overview-window]
            [moon.ui.property-editor-window :as property-editor-window]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn- open-editor!
  [{:keys [ctx/db
           ctx/skin
           ctx/stage
           ctx/textures]}
   property-type]
  (.addActor stage
             (property-overview-window/create
              {:db db
               :textures textures
               :skin skin
               :property-type property-type
               :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                (.addActor stage
                                           (property-editor-window/create
                                            {:ctx ctx
                                             :property (db/get-raw db id)})))})))

(defn create
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}]
  (let [open-editor (fn [db]
                      {:label "Editor"
                       :items (for [property-type (sort (db/property-types db))]
                                {:label (str/capitalize (name property-type))
                                 :on-click (fn [_actor ctx]
                                             ; just pass open editor function
                                             (open-editor! ctx property-type))})})


        ctx-data-viewer {:label "Ctx Data"
                         :items [{:label "Show data"
                                  :on-click (fn [_actor {:keys [ctx/skin
                                                                ctx/stage] :as ctx}]
                                              (.addActor stage
                                                         (data-viewer-window/create
                                                          {:title "Data View"
                                                           :data ctx
                                                           :width 500
                                                           :height 500
                                                           :skin skin})))}]}
        help-info-text {:label "Help"
                        :items [{:label input/info-text}]}
        select-world {:label "Select World"
                      :items (for [world-fn ["world_fns/vampire.edn"
                                             "world_fns/uf_caves.edn"
                                             "world_fns/modules.edn"]]
                               {:label (str "Start " world-fn)
                                :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
                                            (let [rebuild-actors! nil
                                                  #_(fn rebuild-actors! [stage ctx]
                                                      (.clear stage)
                                                      ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                                  create-world nil
                                                  #_(requiring-resolve 'moon.application.create.world/step)
                                                  ui stage
                                                  stage (.getStage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                              (rebuild-actors! ui ctx)
                                              #_(Disposable/.dispose (:world/tiled-map (:ctx/world ctx)))
                                              (set! (.ctx stage) (create-world ctx world-fn))))})}
        update-labels [{:label "elapsed-time"
                        :update-fn (fn [{:keys [ctx/elapsed-time]}]
                                     (str (utils/readable-number elapsed-time) " seconds"))
                        :icon "images/clock.png"}
                       {:label "FPS"
                        :update-fn (fn [{:keys [ctx/graphics]}]
                                     (Graphics/.getFramesPerSecond graphics))
                        :icon "images/fps.png"}
                       {:label "Mouseover-entity id"
                        :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                                     (when-let [entity (and mouseover-eid @mouseover-eid)]
                                       (:entity/id entity)))
                        :icon "images/mouseover.png"}
                       {:label "paused?"
                        :update-fn :ctx/paused?}
                       {:label "GUI"
                        :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                                     (mapv int ui-mouse-position))}
                       {:label "World"
                        :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                                     (mapv int world-mouse-position))}
                       {:label "Zoom"
                        :update-fn (fn [{:keys [ctx/world-viewport]}]
                                     (camera/zoom (Viewport/.getCamera world-viewport)))
                        :icon "images/zoom.png"}]]
    (dev-menu/create
     {:menus [ctx-data-viewer
              (open-editor db)
              help-info-text
              select-world]
      :update-labels (for [item update-labels]
                       (if (:icon item)
                         (update item :icon #(get textures %))
                         item))
      :skin skin})))
