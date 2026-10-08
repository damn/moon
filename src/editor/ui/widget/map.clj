(ns editor.ui.widget.map
  (:require [clojure.set :as set]
            [moon.coll :as coll]
            [moon.map-schema :as map-schema]
            [moon.schema :as schema])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(def ^:private property-k-sort-order
  [:property/id
   :property/pretty-name
   :entity/image
   :entity/animation
   :entity/species
   :creature/level
   :entity/width
   :entity/height
   :entity/flying?
   :item/slot
   :projectile/speed
   :projectile/max-range
   :projectile/piercing?
   :skill/action-time-modifier-key
   :skill/action-time
   :skill/start-action-sound
   :skill/cost])

(defn- k-label-text [k]
  (name k))

(defn value [table schemas widget-value]
  (into {}
        (for [widget (filter (comp vector? (fn [^Actor a] (.getUserObject a))) (.getChildren ^Group table))
              :let [[k _] (.getUserObject ^Actor widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defn- component-row-table
  [{:keys [display-remove-component-button?
           k
           state
           table]}]
  (let [{:keys [skin]} @state
        row-table (doto (Table.)
                    (#(.pad (.defaults ^Table %) (float 2))))]
    (doto (.add row-table ^Actor (when display-remove-component-button?
                                   (doto (TextButton. "-" skin)
                                     (.addListener (proxy [ChangeListener] []
                                                     (changed [event _actor]
                                                       (.remove ^Actor (first (filter (fn [actor]
                                                                                        (and (.getUserObject ^Actor actor)
                                                                                             (= k ((.getUserObject ^Actor actor) 0))))
                                                                                      (.getChildren ^Group table))))
                                                       ((:rebuild-editor-window! @state))))))))
      (.left))
    (.add row-table ^Actor (Label. ^String (k-label-text k) skin))
    (.row ^Table row-table)
    (.pack row-table)
    row-table))

(defn- add-component-row! [table opts]
  (doto (.add ^Table table ^Actor (component-row-table opts))
    (.right))
  (let [^Actor empty nil]
    (doto (.add ^Table table empty)
      (.padTop (float 2))
      (.padBottom (float 2))
      (.fillY)
      (.expandY)))
  (doto (.add ^Table table ^Actor (:editor-widget opts))
    (.left))
  (.row ^Table table))

(defn- add-horiz-sep! [table colspan]
  (let [^Actor empty nil]
    (doto (.add ^Table table empty)
      (.padTop (float 2))
      (.padBottom (float 2))
      (.colspan (int colspan))
      (.fillX)
      (.expandX)))
  (.row ^Table table))

(defn- add-component-window [state schema map-widget-table build-widget widget-value]
  (let [{:keys [schemas skin]} @state
        window (Window. "Choose" skin)
        remaining-ks (sort (remove (set (keys (widget-value schema map-widget-table schemas)))
                                   (map-schema/map-keys (schema/malli-form schema schemas))))]
    (.pad (.defaults window) (float 5))
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    (doseq [k remaining-ks]
      (.add window (doto (TextButton. (name k) skin)
                     (.addListener (proxy [ChangeListener] []
                                     (changed [event _actor]
                                       (.remove window)
                                       (add-component-row!
                                        map-widget-table
                                        {:editor-widget (build-widget (get schemas k)
                                                                      k
                                                                      (let [schema (get schemas k)]
                                                                        (cond
                                                                          (#{:s/map} (schema 0)) {}
                                                                          :else nil)))
                                         :k k
                                         :state state
                                         :display-remove-component-button? (map-schema/optional? (schema/malli-form schema schemas) k)
                                         :table map-widget-table})
                                       ((:rebuild-editor-window! @state)))))))
      (.row window))
    (.pack window)
    window))

(defn- map-widget-table-create
  [{:keys [schema
           k->widget
           k->optional?
           ks-sorted
           opt?
           state
           build-widget
           widget-value]}]
  (let [{:keys [skin stage]} @state
        table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 5)))
                (.setName "moon.db.schema.map.ui.widget"))
        colspan 3]
    (when opt?
      (doto (.add table ^Actor (doto (TextButton. "Add component" skin)
                                 (.addListener (proxy [ChangeListener] []
                                                 (changed [event actor]
                                                   (.addActor ^Stage stage
                                                    (add-component-window state schema table build-widget widget-value)))))))
        (.colspan (int colspan)))
      (.row table)
      (add-horiz-sep! table colspan))
    (doseq [[i k] (map-indexed vector ks-sorted)]
      (when (pos? i)
        (add-horiz-sep! table colspan))
      (add-component-row! table
                          {:editor-widget (k->widget k)
                           :k k
                           :state state
                           :display-remove-component-button? (k->optional? k)
                           :table table}))
    table))

(defn create [state schema m build-widget widget-value]
  (let [schemas (:schemas @state)]
    (map-widget-table-create
     {:schema schema
      :build-widget build-widget
      :widget-value widget-value
      :state state
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget (get schemas k) k v)]))
      :k->optional? #(map-schema/optional? (schema/malli-form schema schemas) %)
      :ks-sorted (map first (coll/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (map-schema/optional-keyset (schema/malli-form schema schemas))
                                 (set (keys m))))})))
