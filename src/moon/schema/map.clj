(ns moon.schema.map
  (:require [clojure.set :as set]
            [malli.utils :as mu]
            [moon.schema :as schema]
            [moon.schemas :as schemas]
            [moon.ui.property-editor-window :as property-editor-window]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window]
            [moon.order :as order])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               Window)))

(defn malli-form [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defn create-value [_ v db]
  (schemas/build-values (:db/schemas db) v db))

(defn- map-widget-table-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? Actor/.getUserObject) (Group/.getChildren table))
              :let [[k _] (Actor/.getUserObject widget)]]
          [k (schema/value (get schemas k) widget schemas)])))

(defn- build-value-widget [ctx schema k v]
  (let [widget (schema/create schema v ctx)] ; - wait its used also somewhere else w/o this schema/create?
    ; FIXME assert no user object !
    (Actor/.setUserObject widget [k v])
    widget))

(defn- rebuild!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   Stage/.getRoot
                   (Group/.findActor "moon.ui.editor.window"))
        map-widget-table (-> window
                             (Group/.findActor "moon.ui.widget.scroll-pane-table")
                             (Group/.findActor "scroll-pane-table")
                             (Group/.findActor "moon.db.schema.map.ui.widget"))
        property (map-widget-table-value map-widget-table (:db/schemas db))]
    (Actor/.remove window)
    (Stage/.addActor stage
                     (property-editor-window/create
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
            {:cell-defaults {:pad 2}
             :rows [[{:actor (when display-remove-component-button?
                               (text-button/create
                                {:text "-"
                                 :on-clicked (fn [_actor ctx]
                                               (Actor/.remove (first (filter (fn [actor]
                                                                               (and (Actor/.getUserObject actor)
                                                                                    (= k ((Actor/.getUserObject actor) 0))))
                                                                             (Group/.getChildren table))))
                                               (rebuild! ctx))
                                 :skin skin}))
                      :left? true}
                     {:actor (Label. ^String label-text ^Skin skin)}]]})
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
  (let [window (doto (window/create
                      {:skin skin
                       :title "Choose"})
                 (table/set-opts! {:cell-defaults {:pad 5}})
                 (.setModal true))
        remaining-ks (sort (remove (set (keys (schema/value schema map-widget-table schemas)))
                                   (mu/map-keys (schema/malli-form schema schemas))))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (text-button/create
                 {:text (name k)
                  :on-clicked (fn [_actor ctx]
                                (Actor/.remove window)
                                (table/add-rows! map-widget-table [(component-row skin
                                                                                  (build-value-widget ctx
                                                                                                      (get schemas k)
                                                                                                      k
                                                                                                      (schemas/default-value schemas k))
                                                                                  k
                                                                                  (mu/optional? k (schema/malli-form schema schemas))
                                                                                  map-widget-table)])
                                (rebuild! ctx))
                  :skin skin})}]))
    (Window/.pack window)
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
  (let [table (doto (table/create {:cell-defaults {:pad 5}})
                (.setName "moon.db.schema.map.ui.widget"))
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
                                         (Stage/.addActor
                                          stage
                                          (add-component-window
                                           {:skin skin
                                            :schemas (:db/schemas db)
                                            :schema schema
                                            :map-widget-table table})))
                           :skin skin})
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
