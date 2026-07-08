(ns clojure.editor.main-window
  (:require [clojure.actor.find-ancestor :refer [find-ancestor]]
            [clojure.actor.get-height]
            [clojure.actor.get-stage]
            [clojure.actor.get-user-object]
            [clojure.actor.get-width]
            [clojure.actor.remove-actor]
            [clojure.actor.set-user-object]
            [clojure.add-close-button :as add-close-button]
            [clojure.add-listener]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.animation-image-button :as animation-image-button]
            [clojure.checkbox :as checkbox]
            [clojure.ctx-do :refer [do!]]
            [clojure.db-update :refer [update!]]
            [clojure.default-value :refer [default-value]]
            [clojure.delete :refer [delete!]]
            [clojure.editor.property-k-sort-order :refer [property-k-sort-order]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [clojure.editor.widget-value :refer [widget-value map-widget-table-get-value]]
            [clojure.edn :as edn]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.event :as event]
            [clojure.get-raw :refer [get-raw]]
            [clojure.group :as group]
            [clojure.horiz-sep :as horiz-sep]
            [clojure.image :as image]
            [clojure.image-button :as image-button]
            [clojure.interpose-f :refer [interpose-f]]
            [clojure.k-label-text :as k-label-text]
            [clojure.key-just-pressed :as key-just-pressed?]
            [clojure.moon-textures :as textures]
            [clojure.optional :refer [optional?]]
            [clojure.pack! :as pack!]
            [clojure.property-image :as property-image]
            [clojure.property-types :refer [property-types]]
            [clojure.scene2d-actor :as actor]
            [clojure.schemas-map-keys :refer [map-keys]]
            [clojure.schemas-optional-keyset :refer [optional-keyset]]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.select-box :as gdx-select-box]
            [clojure.set :as set]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.stage :as stage]
            [clojure.string :as str]
            [clojure.text-field :as gdx-text-field]
            [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.tooltip :as tooltip]
            [clojure.truncate :refer [truncate]]
            [clojure.type :refer [property->type]]
            [clojure.ui-label :as label]
            [clojure.ui-scroll-pane :as scroll-pane]
            [clojure.ui-select-box :as select-box]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.ui-window :as window]
            [clojure.utils-change-listener :as change-listener]
            [clojure.window :as gdx-window]
            [clojure.with-window-close :as with-window-close]))

(defmulti ^:private create-widget
  (fn [[schema-k :as _schema] v ctx]
    schema-k))

(defn- build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (clojure.actor.set-user-object/f widget [k v])
    widget))


(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (clojure.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
    (pack!/f (find-ancestor table (partial instance? gdx-window/class)))
    (let [[k _] (clojure.actor.get-user-object/f table)]
      (clojure.actor.set-user-object/f table [k sound-name]))))

(defn- open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (doto (window/create
                             {:title "Choose"
                              :skin skin
                              :table/rows
                              [[(let [table (table/create
                                             {:table/cell-defaults {:pad 5}
                                              :table/rows (for [sound-name (map first (:ctx/audio ctx))]
                                                            [{:actor (doto (text-button/create
                                                                            {:text sound-name
                                                                             :skin skin})
                                                                           (clojure.add-listener/f (change-listener/create
                                                                                                    (fn [event actor]
                                                                                                      ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/get-stage event)))))))}
                                                             {:actor (doto (text-button/create
                                                                            {:text "play!"
                                                                             :skin skin})
                                                                           (clojure.add-listener/f (change-listener/create
                                                                                                    (fn [event _actor]
                                                                                                      (do! (:stage/ctx (event/get-stage event))
                                                                                                           [[:tx/sound sound-name]])))))}])} )]
                                {:actor (scroll-pane/create
                                         {:actor table
                                          :skin skin})
                                 :width  (+ (clojure.actor.get-width/f table) 50)
                                 :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                              (clojure.actor.get-height/f table))})]]})
                            (add-close-button/f! skin)
                            (gdx-window/set-modal! true)))))

(defn- sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                   {:text sound-name
                    :skin skin})
             (clojure.add-listener/f (change-listener/create
                                      (fn [event _actor]
                                        ((open-select-sounds-handler table sound-columns)
                                         (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (clojure.add-listener/f (change-listener/create
                                      (fn [event _actor]
                                        (do! (:stage/ctx (event/get-stage event))
                                             [[:tx/sound sound-name]])))))}])

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (pack!/f (find-ancestor table (partial instance? gdx-window/class))))]
    (add-rows!
     table
     [[(when-not property-id
         {:actor (doto (text-button/create {:text "+" :skin skin})
                   (clojure.add-listener/f (change-listener/create
                                            (fn [event _actor]
                                              (let [{:keys [ctx/db
                                                            ctx/skin
                                                            ctx/stage
                                                            ctx/textures]
                                                     :as ctx} (:stage/ctx (event/get-stage event))]
                                                (stage/add-actor!
                                                 stage
                                                 (property-overview-window
                                                  {:db db
                                                   :textures textures
                                                   :skin skin
                                                   :property-type property-type
                                                   :clicked-id-fn (fn [actor id ctx]
                                                                    (clojure.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
                                                                    (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (get-raw db property-id)]
           {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                     (clojure.add-listener/f (text-tooltip/create (tooltip/f property) skin))
                     (clojure.actor.set-user-object/f property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create
                        {:text "-"
                         :skin skin})
                   (clojure.add-listener/f (change-listener/create
                                            (fn [event _actor]
                                              (redo-rows (:stage/ctx (event/get-stage event))
                                                         nil)))))})]])))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (pack!/f (find-ancestor table (partial instance? gdx-window/class))))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (clojure.add-listener/f (change-listener/create
                                          (fn [event _actor]
                                            (let [{:keys [ctx/db
                                                          ctx/skin
                                                          ctx/stage
                                                          ctx/textures]
                                                   :as ctx} (:stage/ctx (event/get-stage event))]
                                              (stage/add-actor!
                                               stage
                                               (property-overview-window
                                                {:db db
                                                 :textures textures
                                                 :skin skin
                                                 :property-type property-type
                                                 :clicked-id-fn (fn [actor id ctx]
                                                                  (clojure.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
                                                                  (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                    (clojure.add-listener/f (text-tooltip/create (tooltip/f property) skin))
                    (clojure.actor.set-user-object/f property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (clojure.add-listener/f (change-listener/create
                                           (fn [event _actor]
                                             (redo-rows (:stage/ctx (event/get-stage event))
                                                        (disj property-ids id))))))})])))
(defn- property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property->type property))
        widget (create-widget schema property ctx)
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close/f (fn [db]
                                                 (delete! db property-id)))
        clicked-save-fn (with-window-close/f (fn [db]
                                               (update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create
                                          {:text "Save [LIGHT_GRAY](ENTER)[]"
                                           :skin skin})
                                     (clojure.add-listener/f (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (clojure.add-listener/f (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-delete-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}]]]
    (doto (window/create
           {:title "[SKY]Property[]"
            :skin skin
            :table/cell-defaults {:pad 5}
            :table/rows [[(scroll-pane-cell/create
                           (table/create {:table/cell-defaults {:pad 5}
                                          :table/rows scroll-pane-rows})
                           skin
                           scroll-pane-height
                           50)]]})
      (add-close-button/f! skin)
      (gdx-window/set-modal! true)
      (group/add-actor! (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (clojure.actor.get-stage/f this)]
                                    (let [ctx (:stage/ctx stage)]
                                      (when (key-just-pressed?/f (:ctx/input ctx) :input.keys/enter)
                                        (clicked-save-fn this ctx)))))}))
      (clojure.actor.set-name/f "moon.ui.clojure.editor-window"))))

(defn- rebuild-editor-window!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.clojure.editor-window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (clojure.actor.remove-actor/f window)
    (stage/add-actor! stage
                      (property-editor-window
                       {:ctx ctx
                        :property property}))))


(defn- create-component-row
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (table/create
            {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (doto (text-button/create
                                            {:text "-"
                                             :skin skin})
                                       (clojure.add-listener/f (change-listener/create
                                                                (fn [event _actor]
                                                                  (clojure.actor.remove-actor/f (first (filter (fn [actor]
                                                                                                          (and (clojure.actor.get-user-object/f actor)
                                                                                                               (= k ((clojure.actor.get-user-object/f actor) 0))))
                                                                                                        (group/get-children table))))
                                                                  (let [ctx (:stage/ctx (event/get-stage event))]
                                                                    (rebuild-editor-window! ctx)))))))
                            :left? true}
                           {:actor (label/create
                                    {:text (k-label-text/f k)
                                     :skin skin})}]]})
    :right? true}
   {:actor nil
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])

(defn- add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (window/create
                      {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}})
                 (add-close-button/f! skin)
                 (gdx-window/set-modal! true))
        remaining-ks (sort (remove (set (keys (widget-value schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (clojure.add-listener/f (change-listener/create
                                           (fn [event _actor]
                                             (clojure.actor.remove-actor/f window)
                                             (let [ctx (:stage/ctx (event/get-stage event))]
                                               (add-rows! map-widget-table [(create-component-row
                                                                            {:skin skin
                                                                             :editor-widget (build-widget ctx
                                                                                                          (get schemas k)
                                                                                                          k
                                                                                                          (default-value schemas k))
                                                                             :k k
                                                                             :display-remove-component-button? (optional? schemas schema k)
                                                                             :table map-widget-table})])
                                               (rebuild-editor-window! ctx))))))}]))
    (pack!/f window)
    window))

(defn- map-widget-table-create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (doto (table/create
                     {:table/cell-defaults {:pad 5}})
                (clojure.actor.set-name/f "moon.db.schema.map.ui.widget"))
        colspan 3
        component-rows (interpose-f (horiz-sep/f colspan)
                                    (map (fn [k]
                                           (create-component-row
                                            {:skin skin
                                             :editor-widget (k->widget k)
                                             :k k
                                             :display-remove-component-button? (k->optional? k)
                                             :table table}))
                                         ks-sorted))]
    (add-rows!
     table
     (concat [(when opt?
                [{:actor (doto (text-button/create
                                {:text "Add component"
                                 :skin skin})
                           (clojure.add-listener/f (change-listener/create
                                                    (fn [event actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/stage
                                                                    ctx/skin]} (:stage/ctx (event/get-stage event))]
                                                        (stage/add-actor!
                                                         stage
                                                         (add-component-window
                                                          {:skin skin
                                                           :schemas (:db/schemas db)
                                                           :schema schema
                                                           :map-widget-table table})))))))
                  :colspan colspan}])]
             [(when opt?
                [{:actor nil
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))



(defmethod create-widget :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (animation-image-button/f textures image 2)})]}))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (checkbox/new "" skin)
    (checkbox/set-checked! checked?)))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defmethod create-widget :s/image
  [_ image {:keys [ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                                              (* scale (texture-region/get-region-height texture-region)))))))

(defmethod create-widget :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (map-widget-table-create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod create-widget :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))

(defmethod create-widget :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod create-widget :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod create-widget :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (clojure.add-listener/f (change-listener/create
                                                     (fn [event _actor]
                                                       ((open-select-sounds-handler table sound-columns)
                                                        (:stage/ctx (event/get-stage event)))))))}])])
    table))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))

(defmethod create-widget :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))





(defn f
  [{:keys [ctx/db
           ctx/skin]}]
  (window/create
   {:title "Edit"
    :skin skin
    :table/rows (for [property-type (sort (property-types db))]
                  [{:actor (doto (text-button/create
                                  {:text (str/capitalize (name property-type))
                                   :skin skin})
                             (clojure.add-listener/f (change-listener/create
                                                      (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/get-stage event))]
                                                          (stage/add-actor! stage
                                                                            (property-overview-window
                                                                             {:db db
                                                                              :textures textures
                                                                              :skin skin
                                                                              :property-type property-type
                                                                              :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                                (stage/add-actor! stage
                                                                                                                  (property-editor-window
                                                                                                                   {:ctx ctx
                                                                                                                    :property (get-raw db id)})))})))))))}])}))
