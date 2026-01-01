(ns moon.ui.editor.window
  (:require [clojure.set :as set]
            [gdl.ui.actor :as actor]
            [gdl.ui.group :as group]
            [gdl.ui.label :as label]
            [gdl.ui.stage :as stage]
            [gdl.ui.widget-group :as widget-group]
            [malli.utils :as mu]
            [moon.db.schemas :as schemas]
            [moon.malli :as malli]
            [moon.ui.editor.schema :as schema]
            [moon.ui.separator :as separator]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window]
            [moon.utils :as utils]))

(defn- map-widget-table-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor/user-object) (group/children table))
              :let [[k _] (actor/user-object widget)]]
          [k (schema/value (get schemas k) widget schemas)])))

(defn- build-value-widget [ctx schema k v]
  (let [widget (schema/create schema v ctx)]
    ; FIXME assert no user object !
    (actor/set-user-object! widget [k v])
    widget))

(defn- rebuild!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   stage/root
                   (group/find-actor "moon.ui.editor.window"))
        map-widget-table (-> window
                             (group/find-actor "moon.ui.widget.scroll-pane-table")
                             (group/find-actor "scroll-pane-table")
                             (group/find-actor "moon.db.schema.map.ui.widget"))
        property (map-widget-table-value map-widget-table (:db/schemas db))]
    (actor/remove! window)
    (stage/add-actor! stage {:type :actor/editor-window
                             :ctx ctx
                             :property property})))

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
            {:cell-defaults {:pad 2}
             :rows [[{:actor (when display-remove-component-button?
                               (text-button/create
                                {:text "-"
                                 :on-clicked (fn [_actor ctx]
                                               (actor/remove! (first (filter (fn [actor]
                                                                               (and (actor/user-object actor)
                                                                                    (= k ((actor/user-object actor) 0))))
                                                                             (group/children table))))
                                               (rebuild! ctx))
                                 :skin skin}))
                      :left? true}
                     {:actor (label/create label-text skin)}]]})
    :right? true}
   {:actor (separator/vertical)
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

(defmethod stage/build :actor/add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (window/create
                {:skin skin
                 :title "Choose"
                 :modal? true
                 :close-button? true
                 :center? true
                 :close-on-escape? true
                 :cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (moon.ui.editor.schema/value schema map-widget-table schemas)))
                                   (mu/map-keys (malli/form schema schemas))))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (text-button/create
                 {:text (name k)
                  :on-clicked (fn [_actor ctx]
                                (actor/remove! window)
                                (table/add-rows! map-widget-table [(component-row skin
                                                                                  (build-value-widget ctx
                                                                                                      (get schemas k)
                                                                                                      k
                                                                                                      (schemas/default-value schemas k))
                                                                                  k
                                                                                  (mu/optional? k (malli/form schema schemas))
                                                                                  map-widget-table)])
                                (rebuild! ctx))
                  :skin skin})}]))
    (widget-group/pack! window)
    window))

(defn- horiz-sep [colspan]
  (fn []
    [{:actor (separator/horizontal)
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
               {:cell-defaults {:pad 5}
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
                           :on-clicked (fn [_actor {:keys [ctx/db
                                                           ctx/stage
                                                           ctx/skin]}]
                                         (stage/add-actor!
                                          stage
                                          {:type :actor/add-component-window
                                           :skin skin
                                           :schemas (:db/schemas db)
                                           :schema schema
                                           :map-widget-table table}))
                           :skin skin})
                  :colspan colspan}])]
             [(when opt?
                [{:actor (separator/horizontal)
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

(defmethod schema/create :s/map
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
      :k->optional? #(mu/optional? % (malli/form schema schemas))
      :ks-sorted (map first (utils/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (mu/optional-keyset (malli/form schema schemas))
                                 (set (keys m))))})))

(defmethod schema/value :s/map
  [_ table schemas]
  (map-widget-table-value table schemas))
