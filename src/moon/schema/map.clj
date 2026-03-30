(ns moon.schema.map
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clojure.set :as set]
            [moon.malli :as mu]
            [moon.order :as order]
            [moon.property-editor-window :as property-editor-window]
            [moon.schema :as schema]
            [moon.schemas :as schemas]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               Table
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (moon Stage)))

(defn malli-form [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defn create-value [_ v db]
  (schemas/build-values (:db/schemas db) v db))

(defn- map-widget-table-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? Actor/.getUserObject) (group/children table))
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
                   (group/find-actor "moon.ui.editor.window"))
        map-widget-table (-> window
                             (group/find-actor "moon.ui.widget.scroll-pane-table")
                             (group/find-actor "scroll-pane-table")
                             (group/find-actor "moon.db.schema.map.ui.widget"))
        property (map-widget-table-value map-widget-table (:db/schemas db))]
    (Actor/.remove window)
    (stage/add-actor! stage
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
  [{:actor (doto (Table.)
             (table/set-cell-defaults! {:pad 2})
             (table/add-rows! [[{:actor (when display-remove-component-button?
                                          (doto (TextButton. "-" ^Skin skin)
                                            (.addListener
                                             (proxy [ChangeListener] []
                                               (changed [event _actor]
                                                 (Actor/.remove (first (filter (fn [actor]
                                                                                 (and (Actor/.getUserObject actor)
                                                                                      (= k ((Actor/.getUserObject actor) 0))))
                                                                               (group/children table))))
                                                 (rebuild! (.ctx ^Stage (Event/.getStage event))))))))
                                 :left? true}
                                {:actor (Label. ^String label-text ^Skin skin)}]]))
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
  (let [window (doto (Window. "Choose" ^Skin skin)
                 (window/add-close-button! skin)
                 (table/set-cell-defaults! {:pad 5})
                 (.setModal true))
        remaining-ks (sort (remove (set (keys (schema/value schema map-widget-table schemas)))
                                   (mu/map-keys (schema/malli-form schema schemas))))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (TextButton. (name k) ^Skin skin)
                  (.addListener
                   (proxy [ChangeListener] []
                     (changed [^Event event _actor]
                       (Actor/.remove window)
                       (let [ctx (.ctx ^Stage (.getStage event))]
                         (table/add-rows! map-widget-table [(component-row skin
                                                                           (build-value-widget ctx
                                                                                               (get schemas k)
                                                                                               k
                                                                                               (schemas/default-value schemas k))
                                                                           k
                                                                           (mu/optional? k (schema/malli-form schema schemas))
                                                                           map-widget-table)])
                         (rebuild! ctx))))))}]))
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
  (let [table (doto (Table.)
                (table/set-cell-defaults! {:pad 5})
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
                [{:actor (doto (TextButton. "Add component" ^Skin skin)
                           (.addListener
                            (proxy [ChangeListener] []
                              (changed [^Event event actor]
                                (let [{:keys [ctx/db
                                              ctx/stage
                                              ctx/skin]} (.ctx ^Stage (.getStage event))]
                                  (stage/add-actor!
                                   stage
                                   (add-component-window
                                    {:skin skin
                                     :schemas (:db/schemas db)
                                     :schema schema
                                     :map-widget-table table})))))))
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
