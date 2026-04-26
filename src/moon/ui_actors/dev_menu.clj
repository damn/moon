(ns moon.ui-actors.dev-menu
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.group :as group]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.ui.table :as table]

            [clojure.string :as str]
            [moon.db :as db]
            [moon.number :as number]
            [clojure.graphics :as graphics]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            ))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:type :ui/actor
    :act! (fn [this _delta]
            (when-let [stage (actor/stage this)]
              (label/set-text! label (text-fn (stage/ctx stage)))))}))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (actor/create {:type :ui/label
                              :text ""
                              :skin skin})
         sub-table (actor/create
                    {:type :ui/table
                     :table/rows [[{:actor (actor/create
                                            {:type :ui/image
                                             :content icon})}
                                   label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add! table {:actor sub-table
                        :right? true
                        :expand-x? true})))
  ([skin table text-fn]
   (let [label (actor/create {:type :ui/label
                              :text ""
                              :skin skin})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add! table {:actor label
                        :right? true
                        :expand-x? true}))))

(defn- create-window [skin label items]
  (actor/create
   {:type :ui/window
    :title label
    :skin skin
    :window/close-button? skin
    :table/rows [(for [{:keys [label on-click]} items]
                   {:actor
                    (actor/create
                     {:type :ui/text-button
                      :text label
                      :skin skin
                      :actor/listeners {:listener/change (fn [event actor]
                                                           (on-click actor (stage/ctx (event/stage event))))}})})]}))

(defn- main-table [skin menus update-labels]
  (let [table (actor/create
               {:type :ui/table
                :table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (actor/create
                                 {:type :ui/text-button
                                  :text label
                                  :skin skin
                                  :actor/listeners {:listener/change (fn [event actor]
                                                                       (stage/add-actor! (event/stage event) (create-window skin label items)))}})})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn- create*
  [{:keys [menus update-labels skin]}]
  (actor/create
   {:type :ui/table
    :table/rows [[{:actor (main-table skin menus update-labels)
                   :expand-x? true
                   :fill-x? true
                   :colspan 1}]
                 [{:actor (actor/create
                           {:type :ui/label
                            :text ""
                            :skin skin
                            :actor/touchable :touchable/disabled})
                   :expand? true
                   :fill-x? true
                   :fill-y? true}]]
    :widget-group/fill-parent? true}))

(defn create
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]
    :as ctx}]
  (create*
   {:menus [
            {:label "Ctx Data"
             :items [{:label "Show data"
                      :on-click (fn [_actor {:keys [ctx/skin
                                                    ctx/stage] :as ctx}]
                                  (stage/add-actor! stage
                                                    (actor/create
                                                     {:type :ui/data-viewer-window
                                                      :title "Data View"
                                                      :data ctx
                                                      :width 1000
                                                      :height 1000
                                                      :skin skin})))}]}
            {:label "Editor"
             :items (for [property-type (sort (db/property-types db))]
                      {:label (str/capitalize (name property-type))
                       :on-click (fn [_actor {:keys [ctx/db
                                                     ctx/skin
                                                     ctx/stage
                                                     ctx/textures]
                                              :as ctx}]
                                   (stage/add-actor! stage
                                                     (actor/create
                                                      {:type :ui/property-overview-window
                                                       :db db
                                                       :textures textures
                                                       :skin skin
                                                       :property-type property-type
                                                       :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                        (stage/add-actor! stage
                                                                                          (actor/create
                                                                                           {:type :ui/property-editor-window
                                                                                            :ctx ctx
                                                                                            :property (db/get-raw db id)})))})))})}
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [world-fn ["world_fns/vampire.edn"
                                    "world_fns/uf_caves.edn"
                                    "world_fns/modules.edn"]]
                      {:label (str "Start " world-fn)
                       :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
                                   #_(let [rebuild-actors! nil
                                           #_(fn rebuild-actors! [stage ctx]
                                               (.clear stage)
                                               ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                           create-world nil
                                           #_(requiring-resolve 'moon.application.create.world/step)
                                           ui stage
                                           stage (Actor/.getStage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                       (rebuild-actors! ui ctx)
                                       #_(Disposable/.dispose (:ctx/tiled-map ctx))
                                       (set! (.ctx ^Stage stage) (create-world ctx world-fn))))})}

            ]
    :update-labels (for [item [
                               {:label "elapsed-time"
                                :update-fn (fn [{:keys [ctx/elapsed-time]}]
                                             (str (number/readable elapsed-time) " seconds"))
                                :icon "images/clock.png"}
                               {:label "FPS"
                                :update-fn (fn [{:keys [ctx/graphics]}]
                                             (graphics/frames-per-second graphics))
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
                                             (camera/zoom (viewport/camera world-viewport)))
                                :icon "images/zoom.png"}
                               ]]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))
