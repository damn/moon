(ns moon.schema
  (:require clojure.edn
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.event :as event]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.stage :as stage]
            [clojure.scene2d.ui.check-box :as check-box]
            [clojure.scene2d.ui.select-box :as select-box]
            [clojure.scene2d.ui.table :as table]
            [clojure.scene2d.ui.widget-group :as widget-group]
            [clojure.set :as set]
            [moon.edn :as edn]
            [moon.schemas :as schemas]
            [moon.string :as string]
            [moon.textures :as textures]
            [moon.order :as order]
            ))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod create :default
  [_ v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/label
    :text (string/truncate (edn/->str v) 60)
    :skin skin}))

(defmethod value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))

(defmethod create :s/animation
  [_ animation {:keys [ctx/skin
                       ctx/textures]}]
  (actor/create
   {:type :ui/table
    :table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (actor/create
                            {:type :ui/image-button
                             :drawable {:drawable/texture-region (textures/texture-region textures image)
                                        :drawable/scale 2}})})]}))

(defmethod create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/check-box
    :skin skin
    :checked? checked?}))

(defmethod value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

(defmethod create :s/enum [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/select-box
    :skin skin
    :items (map edn/->str (rest schema))
    :selected (edn/->str v)}))

(defmethod value :s/enum [_  widget _schemas]
  (clojure.edn/read-string (select-box/selected widget)))

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
  (actor/create
   {:type :ui/image-button
    :drawable {:drawable/texture-region (textures/texture-region textures image)
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
                      (actor/create
                       {:type :ui/property-editor-window
                        :ctx ctx
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
  [{:actor (actor/create
            {:type :ui/table
             :table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (actor/create
                                      {:type :ui/text-button
                                       :text "-"
                                       :skin skin
                                       :actor/listeners {:listener/change
                                                         (fn [event _actor]
                                                           (actor/remove! (first (filter (fn [actor]
                                                                                           (and (actor/user-object actor)
                                                                                                (= k ((actor/user-object actor) 0))))
                                                                                         (group/children table))))
                                                           (rebuild! (stage/ctx (event/stage event))))}}))
                            :left? true}
                           {:actor (actor/create
                                    {:type :ui/label
                                     :text label-text
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

(defn- add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (actor/create
                {:type :ui/window
                 :title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (value schema map-widget-table schemas)))
                                   (schemas/map-keys schemas schema)))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (actor/create
                 {:type :ui/text-button
                  :skin skin
                  :text (name k)
                  :actor/listeners {:listener/change
                                    (fn [event _actor]
                                      (actor/remove! window)
                                      (let [ctx (stage/ctx (event/stage event))]
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
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}
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
                [{:actor (actor/create
                          {:type :ui/text-button
                           :text "Add component"
                           :skin skin
                           :actor/listeners {:listener/change
                                             (fn [event actor]
                                               (let [{:keys [ctx/db
                                                             ctx/stage
                                                             ctx/skin]} (stage/ctx (event/stage event))]
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
      :ks-sorted (map first (order/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (schemas/optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod value :s/map
  [_ table schemas]
  (map-widget-table-value table schemas))
