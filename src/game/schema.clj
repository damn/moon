(ns game.schema
  (:require [clojure.core-ext :refer [sort-by-k-order
                                      ->edn-str
                                      truncate]]
            moon.ui.property-overview-window
            [clojure.edn :as edn]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.event :as event]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.stage :as stage]
            [clojure.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.check-box :as check-box]
            [gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [moon.ui.error-window]
            [gdx.scenes.scene2d.ui.select-box :as select-box]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-field :as text-field]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdx.input.keys :as input.keys]
            [clojure.set :as set]
            [game.ctx :as ctx]
            [moon.db :as db]
            [moon.schemas :as schemas]
            [moon.property :as property]
            [moon.textures :as textures]
            [moon.throwable :as throwable]))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        ; build for get-widget-value
        ; or find a way to find the widget from the context @ save button
        ; should be possible
        widget (create schema property ctx) ; FIXME here no set user object k v ?
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [actor {:keys [ctx/skin
                                               ctx/stage]
                                        :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     stage (actor/stage actor)]
                                 (stage/set-ctx! stage new-ctx))
                               (actor/remove! (actor/find-ancestor actor ui/window?))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (stage/add-actor! stage
                                                   (moon.ui.error-window/create
                                                    {:type :ui/error-window
                                                     :skin skin
                                                     :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [ctx (:stage/ctx stage)]
                              (when (ctx/key-just-pressed? ctx input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button {:text "Save [LIGHT_GRAY](ENTER)[]"
                     :skin skin
                     :actor/listeners [[:listener/change
                                        (fn [event actor]
                                          (clicked-save-fn actor (:stage/ctx (event/stage event))))]]}
        delete-button {:text "Delete"
                       :skin skin
                       :actor/listeners [[:listener/change
                                          (fn [event actor]
                                            (clicked-delete-fn actor (:stage/ctx (event/stage event))))]]}
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (text-button/create save-button) :center? true}
                           {:actor (text-button/create delete-button) :center? true}]]
        rows [[(let [table (table/create
                            {:table/cell-defaults {:pad 5}
                             :table/rows scroll-pane-rows})]
                 {:actor (scroll-pane/create
                          {:actor table
                           :skin skin})
                  :width  (+ (actor/width table) 50)
                  :height (min (- scroll-pane-height 50)
                               (actor/height table))})]]]
    (window/create
     {:title "[SKY]Property[]"
      :skin skin
      :window/close-button? skin
      :window/modal? true
      :table/cell-defaults {:pad 5}
      :table/rows rows
      :group/actors actors
      :actor/name "moon.ui.editor.window"})))

(defmethod create :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))

(defmethod create :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (image-button/create
                            {:drawable (texture-region-drawable/create*
                                        {:drawable/texture-region (textures/texture-region textures image)
                                         :drawable/scale 2})})})]}))

(defmethod create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))

(defmethod value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

(defmethod create :s/enum [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defmethod value :s/enum [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (texture/region (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defmethod create :s/image
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (image-button/create
   {:drawable {:drawable/texture-region (textures/texture-region textures image)
               :drawable/scale 2}})
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here

(defn- map-widget-table-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor/user-object) (group/children table))
              :let [[k _] (actor/user-object widget)]]
          [k (value (get schemas k) widget schemas)])))

(defn- build-value-widget [ctx schema k v]
  (let [widget (create schema v ctx)] ; - wait its used also somewhere else w/o this schema/create?
    ; FIXME assert no user object !
    (actor/set-user-object! widget [k v])
    widget))

(defn- rebuild!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   (stage/find-actor "moon.ui.editor.window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-value map-widget-table (:db/schemas db))]
    (actor/remove! window)
    (stage/add-actor! stage
                      (property-editor-window
                       {:ctx ctx
                        :property property}))))

(defn- k->label-text [k]
  (name k) ;(str "[GRAY]:" (namespace k) "[]/" (name k))
  )

(defn- component-row*
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table
           label-text]}]
  [{:actor (table/create
            {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (text-button/create
                                      {:text "-"
                                       :skin skin
                                       :actor/listeners {:listener/change
                                                         (fn [event _actor]
                                                           (actor/remove! (first (filter (fn [actor]
                                                                                           (and (actor/user-object actor)
                                                                                                (= k ((actor/user-object actor) 0))))
                                                                                         (group/children table))))
                                                           (rebuild! (:stage/ctx (event/stage event))))}}))
                            :left? true}
                           {:actor (label/create
                                    {:text label-text
                                     :skin skin})}]]})
    :right? true}
   {:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "vertical")
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])

(defn- component-row [skin editor-widget k optional-key? table]
  (component-row*
   {:skin skin
    :editor-widget editor-widget
    :display-remove-component-button? optional-key?
    :k k
    :table table
    :label-text (k->label-text k)}))

(defn add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (window/create
                {:title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (value schema map-widget-table schemas)))
                                   (schemas/map-keys schemas schema)))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (text-button/create
                 {:skin skin
                  :text (name k)
                  :actor/listeners {:listener/change
                                    (fn [event _actor]
                                      (actor/remove! window)
                                      (let [ctx (:stage/ctx (event/stage event))]
                                        (table/add-rows! map-widget-table [(component-row skin
                                                                                          (build-value-widget ctx
                                                                                                              (get schemas k)
                                                                                                              k
                                                                                                              (schemas/default-value schemas k))
                                                                                          k
                                                                                          (schemas/optional? schemas schema k)
                                                                                          map-widget-table)])
                                        (rebuild! ctx)))}})}]))
    (widget-group/pack! window)
    window))

(defn- horiz-sep [colspan]
  (fn []
    [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
      :pad-top 2
      :pad-bottom 2
      :colspan colspan
      :fill-x? true
      :expand-x? true}]))

(defn- interpose-f [f coll]
  (drop 1 (interleave (repeatedly f) coll)))

(defn- create-map-widget-table
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}
                :actor/name "moon.db.schema.map.ui.widget"})
        colspan 3
        component-rows (interpose-f (horiz-sep colspan)
                                    (map (fn [k]
                                           (component-row skin
                                                          (k->widget k)
                                                          k
                                                          (k->optional? k)
                                                          table))
                                         ks-sorted))]
    (table/add-rows!
     table
     (concat [(when opt?
                [{:actor (text-button/create
                          {:text "Add component"
                           :skin skin
                           :actor/listeners {:listener/change
                                             (fn [event actor]
                                               (let [{:keys [ctx/db
                                                             ctx/stage
                                                             ctx/skin]} (:stage/ctx (event/stage event))]
                                                 (stage/add-actor!
                                                  stage
                                                  (add-component-window
                                                   {:skin skin
                                                    :schemas (:db/schemas db)
                                                    :schema schema
                                                    :map-widget-table table}))))}})
                  :colspan colspan}])]
             [(when opt?
                [{:actor  nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))

(def ^:private property-k-sort-order
  [:property/id
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
   :skill/cost
   :skill/cooldown])

(defmethod create :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (create-map-widget-table
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-value-widget ctx (get schemas k) k v)]))
      :k->optional? #(schemas/optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (schemas/optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod value :s/map
  [_ table schemas]
  (map-widget-table-value table schemas))

(defmethod create :s/number
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod value :s/number
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))

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
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[{:actor (text-button/create
                {:text "+"
                 :skin skin
                 :actor/listeners {:listener/change (fn [event _actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/skin
                                                                    ctx/stage
                                                                    ctx/textures]
                                                             :as ctx} (:stage/ctx (event/stage event))]
                                                        (stage/add-actor!
                                                         stage
                                                         (moon.ui.property-overview-window/create
                                                          {:db db
                                                           :textures textures
                                                           :skin skin
                                                           :property-type property-type
                                                           :clicked-id-fn (fn [actor id ctx]
                                                                            (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                            (redo-rows ctx (conj property-ids id)))}))))}})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (image/create
                   {:content (textures/texture-region textures (property/image property))
                    :actor/user-object property-id
                    :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))
      (for [id property-ids]
        {:actor (text-button/create
                 {:text "-"
                  :skin skin
                  :actor/listeners {:listener/change (fn [event _actor]
                                                       (redo-rows (:stage/ctx (event/stage event))
                                                                  (disj property-ids id)))}})})])))

(defmethod create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod value :s/one-to-many [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

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
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (text-button/create
                  {:text "+"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/stage event))]
                                                          (stage/add-actor!
                                                           stage
                                                           (moon.ui.property-overview-window/create
                                                            {:db db
                                                             :textures textures
                                                             :skin skin
                                                             :property-type property-type
                                                             :clicked-id-fn (fn [actor id ctx]
                                                                              (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                              (redo-rows ctx id))}))))}})})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (image/create
                    {:content (textures/texture-region textures (property/image property))
                     :actor/user-object property-id
                     :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))]
      [(when property-id
         {:actor (text-button/create
                  {:text "-"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (redo-rows (:stage/ctx (event/stage event))
                                                                   nil))}})})]])))

(defmethod create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod value :s/one-to-one [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor ui/window?))
    (widget-group/pack! (actor/find-ancestor table ui/window?))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (window/create
                       {:title "Choose"
                        :skin skin
                        :window/close-button? skin
                        :window/modal? true
                        :table/rows
                        [[(let [table (table/create
                                       {:table/cell-defaults {:pad 5}
                                        :table/rows (for [sound-name (ctx/sound-names ctx)]
                                                      [{:actor (text-button/create
                                                                {:text sound-name
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change
                                                                                   (fn [event actor]
                                                                                     ((rebuild-sound-widget! table sound-name) actor (:stage/ctx (event/stage event))))}})}
                                                       {:actor (text-button/create
                                                                {:text "play!"
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change (fn [event _actor]
                                                                                                      (ctx/do! (:stage/ctx (event/stage event))
                                                                                                               [[:tx/sound sound-name]]))}})}])} )]
                            {:actor (scroll-pane/create
                                     {:actor table
                                      :skin skin})
                             :width  (+ (actor/width table) 50)
                             :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                          (actor/height table))})]]}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}
   {:actor (text-button/create
            {:text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (ctx/do! (:stage/ctx (event/stage event))
                                                           [[:tx/sound sound-name]]))}})}])

(defmethod create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor
                                (text-button/create
                                 {:text "No sound"
                                  :skin skin
                                  :actor/listeners {:listener/change
                                                    (fn [event _actor]
                                                      ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}])])
    table))

(defmethod create :s/string [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod value :s/string [_ widget _schemas]
  (text-field/text widget))

(defmethod create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod value :s/val-max
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
