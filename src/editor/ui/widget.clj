(ns editor.ui.widget
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [clojure.set :as set]
            [editor.ui.overview :refer [property-overview-window]]
            [editor.ui.sound :refer [sound-widget]]
            [editor.ui.util :refer [find-ancestor]]
            [moon.coll :as coll]
            [moon.db :as db]
            [moon.map-schema :as map-schema]
            [moon.schema :as schema]
            [moon.string :as string]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Image ImageButton Label SelectBox Table TextButton TextField TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout TextureRegionDrawable)))

(declare create-widget widget-value)

(defn- map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? (fn [^Actor a] (.getUserObject a))) (.getChildren ^Group table))
              :let [[k _] (.getUserObject ^Actor widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defn widget-value [[schema-k] widget schemas]
  (case schema-k
    :s/boolean (.isChecked ^CheckBox widget)
    :s/enum (edn/read-string (.getSelected ^SelectBox widget))
    :s/map (map-widget-table-get-value widget schemas)
    :s/number (edn/read-string (.getText ^TextField widget))
    :s/one-to-many (->> (.getChildren ^Group widget)
                        (keep (fn [^Actor a] (.getUserObject a)))
                        set)
    :s/one-to-one (->> (.getChildren ^Group widget)
                       (keep (fn [^Actor a] (.getUserObject a)))
                       first)
    :s/string (.getText ^TextField widget)
    :s/val-max (edn/read-string (.getText ^TextField widget))
    ((.getUserObject ^Actor widget) 1)))

(defn map-widget-value [table schemas]
  (map-widget-table-get-value table schemas))

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

(defn- add-one-to-many-rows [state table property-type property-ids]
  (let [{:keys [db skin stage textures]} @state
        redo-rows (fn [property-ids]
                    (.clearChildren ^Group table)
                    (add-one-to-many-rows state table property-type property-ids)
                    (.pack ^Layout (find-ancestor table (partial instance? Window))))]
    (doseq [row
     [[{:actor (doto (TextButton. "+" skin)
                 (.addListener (proxy [ChangeListener] []
                                 (changed [event _actor]
                                   (.addActor ^Stage stage
                                    (property-overview-window
                                     state
                                     property-type
                                     (fn [actor id]
                                       (.remove ^Actor (find-ancestor actor (partial instance? Window)))
                                       (redo-rows (conj property-ids id)))))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (or (:entity/image property)
                                                                                    (first (:animation/frames (:entity/animation property))))))
                    (.addListener (TextTooltip. ^String (binding [*print-level* 2]
                                                          (with-out-str
                                                            (pprint property))) skin))
                    (.setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (TextButton. "-" skin)
                  (.addListener (proxy [ChangeListener] []
                                  (changed [event _actor]
                                    (redo-rows (disj property-ids id))))))})]]
      (doseq [cell row]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table))))

(defn- add-one-to-one-rows [state table property-type property-id]
  (let [{:keys [db skin stage textures]} @state
        redo-rows (fn [id]
                    (.clearChildren ^Group table)
                    (add-one-to-one-rows state table property-type id)
                    (.pack ^Layout (find-ancestor table (partial instance? Window))))]
    (doseq [row (cond-> []
                  (not property-id)
                  (conj [{:actor (doto (TextButton. "+" skin)
                                   (.addListener (proxy [ChangeListener] []
                                                   (changed [event _actor]
                                                     (.addActor ^Stage stage
                                                      (property-overview-window
                                                       state
                                                       property-type
                                                       (fn [actor id]
                                                         (.remove ^Actor (find-ancestor actor (partial instance? Window)))
                                                         (redo-rows id))))))))}])
                  property-id
                  (conj [{:actor (let [property (db/get-raw db property-id)]
                                   (doto (Image. ^TextureRegion (textures/texture-region textures (or (:entity/image property)
                                                                                                     (first (:animation/frames (:entity/animation property))))))
                                     (.addListener (TextTooltip. ^String (binding [*print-level* 2]
                                                                           (with-out-str
                                                                             (pprint property))) skin))
                                     (.setUserObject property-id)))}]
                        [{:actor (doto (TextButton. "-" skin)
                                   (.addListener (proxy [ChangeListener] []
                                                   (changed [event _actor]
                                                     (redo-rows nil)))))}]))]
      (doseq [cell row]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table))))

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

(defn- add-component-window [state schema map-widget-table build-widget]
  (let [{:keys [db skin]} @state
        schemas (:db/schemas db)
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
           build-widget]}]
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
                                                    (add-component-window state schema table build-widget)))))))
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

(defn- scaled-image-button [texture-region scale]
  (ImageButton.
   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))

(defn- default-widget [state v]
  (Label. ^String (string/truncate (binding [*print-level* nil]
                                     (pr-str v))
                                   60)
          (:skin @state)))

(defn- animation-widget [state animation]
  (let [table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 1))))]
    (doseq [image (:animation/frames animation)]
      (.add table ^Actor (scaled-image-button
                          (textures/texture-region (:textures @state) image)
                          2)))
    (.row ^Table table)
    (.pack table)
    table))

(defn- boolean-widget [state checked?]
  (doto (CheckBox. "" (:skin @state))
    (.setChecked checked?)))

(defn- enum-widget [state schema v]
  (doto ^SelectBox (SelectBox. (:skin @state))
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))

(defn- image-widget [state image]
  (scaled-image-button (textures/texture-region (:textures @state) image) 2))

(defn- map-widget [state schema m build-widget]
  (let [schemas (:db/schemas (:db @state))]
    (map-widget-table-create
     {:schema schema
      :build-widget build-widget
      :state state
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget (get schemas k) k v)]))
      :k->optional? #(map-schema/optional? (schema/malli-form schema schemas) %)
      :ks-sorted (map first (coll/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (map-schema/optional-keyset (schema/malli-form schema schemas))
                                 (set (keys m))))})))

(defn- number-widget [state schema v]
  (let [{:keys [skin]} @state]
    (doto (TextField. ^String (pr-str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- one-to-many-widget [state [_ property-type] property-ids]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-many-rows state table property-type property-ids)
    table))

(defn- one-to-one-widget [state [_ property-type] property-id]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-one-rows state table property-type property-id)
    table))

(defn- string-widget [state schema v]
  (let [{:keys [skin]} @state]
    (doto (TextField. ^String (str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- val-max-widget [state schema v]
  (let [{:keys [skin]} @state]
    (doto (TextField. ^String (pr-str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- build-widget [state schema k v]
  (let [widget (create-widget state schema v)]
    (.setUserObject ^Actor widget [k v])
    widget))

(defn create-widget [state [schema-k :as schema] v]
  (case schema-k
    :s/animation (animation-widget state v)
    :s/boolean (boolean-widget state v)
    :s/enum (enum-widget state schema v)
    :s/image (image-widget state v)
    :s/map (map-widget state schema v
                       (fn [schema k v] (build-widget state schema k v)))
    :s/number (number-widget state schema v)
    :s/one-to-many (one-to-many-widget state schema v)
    :s/one-to-one (one-to-one-widget state schema v)
    :s/sound (sound-widget state v)
    :s/string (string-widget state schema v)
    :s/val-max (val-max-widget state schema v)
    (default-widget state v)))
