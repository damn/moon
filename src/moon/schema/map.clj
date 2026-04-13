(ns moon.schema.map
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [clojure.set :as set]
            [moon.actor :as actor]
            [moon.malli :as mu]
            [moon.order :as order]
            [moon.schema :as schema]
            [moon.schemas :as schemas]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.ui :as ui]))

(defn malli-form [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defn create-value [_ v db]
  (schemas/build-values (:db/schemas db) v db))

(defn- map-widget-table-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor/user-object) (group/children table))
              :let [[k _] (actor/user-object widget)]]
          [k (schema/value (get schemas k) widget schemas)])))

(defn- build-value-widget [ctx schema k v]
  (let [widget (schema/create schema v ctx)] ; - wait its used also somewhere else w/o this schema/create?
    ; FIXME assert no user object !
    (actor/set-user-object! widget [k v])
    widget))

(defn- rebuild!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   (stage/find-actor "moon.ui.editor.window"))
        map-widget-table (-> window
                             (group/find-actor "moon.ui.widget.scroll-pane-table")
                             (group/find-actor "scroll-pane-table")
                             (group/find-actor "moon.db.schema.map.ui.widget"))
        property (map-widget-table-value map-widget-table (:db/schemas db))]
    (actor/remove! window)
    (stage/add-actor! stage
                      ((get (:ctx/actor-fns ctx) :ui/property-editor-window)
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
  [{:actor (ui/create
            {:type :ui/table
             :table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (ui/create
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
                           {:actor (ui/create
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
  (let [window (ui/create
                {:type :ui/window
                 :title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (schema/value schema map-widget-table schemas)))
                                   (mu/map-keys (schema/malli-form schema schemas))))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (ui/create
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
                                                                                          (mu/optional? k (schema/malli-form schema schemas))
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
  (let [table (ui/create
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
                [{:actor (ui/create
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

(defn create
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
      :k->optional? #(mu/optional? % (schema/malli-form schema schemas))
      :ks-sorted (map first (order/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (mu/optional-keyset (schema/malli-form schema schemas))
                                 (set (keys m))))})))

(defn value
  [_ table schemas]
  (map-widget-table-value table schemas))
