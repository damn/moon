(ns moon.ui-actors.dev-menu
  (:require [clojure.string :as str]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.ui :as ui]
            [moon.ui.actor :as actor]
            [moon.ui.build.editor-window :as editor-window]
            [moon.ui.dev-menu :as dev-menu]
            [moon.ui.editor.overview-window :as overview-window]
            [moon.ui.stage :as stage]
            [moon.utils :as utils]
            [moon.world :as world]))

(defn- open-editor!
  [{:keys [ctx/db
           ctx/graphics
           ctx/skin
           ctx/stage]}
   property-type]
  (stage/add-actor! stage
                    (overview-window/create
                     {:db db
                      :graphics graphics
                      :skin skin
                      :property-type property-type
                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                       (stage/add-actor! stage
                                                         (editor-window/create
                                                          {:ctx ctx
                                                           :property (db/get-raw db id)})))})))

(defn create
  [{:keys [ctx/db
           ctx/graphics
           ctx/skin]}]
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
                                              (ui/show-data-viewer! stage ctx skin))}]}
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
                                                      (stage/clear! stage)
                                                      ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                                  create-world nil
                                                  #_(requiring-resolve 'moon.application.create.world/step)
                                                  ui stage
                                                  stage (actor/stage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                              (rebuild-actors! ui ctx)
                                              (world/dispose! (:ctx/world ctx))
                                              (stage/set-ctx! stage (create-world ctx world-fn))))})}
        update-labels [{:label "elapsed-time"
                        :update-fn (fn [ctx]
                                     (str (utils/readable-number (:world/elapsed-time (:ctx/world ctx))) " seconds"))
                        :icon "images/clock.png"}
                       {:label "FPS"
                        :update-fn (fn [ctx]
                                     (graphics/frames-per-second (:ctx/graphics ctx)))
                        :icon "images/fps.png"}
                       {:label "Mouseover-entity id"
                        :update-fn (fn [{:keys [ctx/world]}]
                                     (let [eid (:world/mouseover-eid world)]
                                       (when-let [entity (and eid @eid)]
                                         (:entity/id entity))))
                        :icon "images/mouseover.png"}
                       {:label "paused?"
                        :update-fn (comp :world/paused? :ctx/world)}
                       {:label "GUI"
                        :update-fn (fn [{:keys [ctx/graphics]}]
                                     (mapv int (:graphics/ui-mouse-position graphics)))}
                       {:label "World"
                        :update-fn (fn [{:keys [ctx/graphics]}]
                                     (mapv int (:graphics/world-mouse-position graphics)))}
                       {:label "Zoom"
                        :update-fn (fn [ctx]
                                     (graphics/zoom (:ctx/graphics ctx)))
                        :icon "images/zoom.png"}]]
    (dev-menu/create
     {:menus [ctx-data-viewer
              (open-editor db)
              help-info-text
              select-world]
      :update-labels (for [item update-labels]
                       (if (:icon item)
                         (update item :icon #(get (:graphics/textures graphics) %))
                         item))
      :skin skin})))
