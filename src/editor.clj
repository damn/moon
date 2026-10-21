(ns editor
  (:require [clj-commons.pretty.repl :as pretty-repl]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.set :as set]
            [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]
            [moon.coll :as coll]
            [moon.db :as db]
            [moon.map-schema :as map-schema]
            [moon.schema :as schema]
            [moon.string :as string]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input Input$Keys)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics.g2d SpriteBatch TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Image ImageButton Label ScrollPane SelectBox Skin Stack Table TextButton TextField TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(def ^:private property-type->overview-table-props
  {:properties/audiovisuals {:columns 10
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}
   :properties/creatures    {:columns 15
                             :image-scale 1.5
                             :sort-by-fn #(vector (:creature/level %)
                                                  (name (:entity/species %))
                                                  (name (:property/id %)))
                             :extra-info-text #(str (:creature/level %))}
   :properties/items        {:columns 20
                             :image-scale 1.1
                             :sort-by-fn #(vector (name (:item/slot %))
                                                  (name (:property/id %)))
                             :extra-info-text (constantly "")}
   :properties/projectiles  {:columns 16
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}
   :properties/skills       {:columns 16
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}})

(defn- overview-table-rows* [state image-scale rows]
  (let [{:keys [skin]} state]
    (for [row rows]
      (for [{:keys [texture-region
                    on-clicked
                    tooltip
                    extra-info-text]} row]
        {:actor (let [stack (Stack.)]
                  (run! #(.addActor ^Group stack %)
                        [(doto (ImageButton.
                                (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                  (.setMinSize (* image-scale (.getRegionWidth ^TextureRegion texture-region))
                                               (* image-scale (.getRegionHeight ^TextureRegion texture-region)))))
                           (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (on-clicked actor))))
                           (.addListener (TextTooltip. ^String tooltip skin)))
                         (doto (Label. ^String extra-info-text skin)
                           (.setTouchable Touchable/disabled))])
                  stack)}))))

(defn- property-overview-rows [state property-type clicked-id-fn]
  (let [{:keys [db textures]} state
        {:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw @db property-type)
         (sort-by sort-by-fn)
         (map (fn [property]
                {:texture-region (textures/texture-region textures (or (:entity/image property)
                                                                       (first (:animation/frames (:entity/animation property)))))
                 :on-clicked (fn [actor]
                               (clicked-id-fn actor (:property/id property)))
                 :tooltip (binding [*print-level* 2]
                            (with-out-str
                              (pprint property)))
                 :extra-info-text (extra-info-text property)}))
         (partition-all columns)
         (overview-table-rows* state image-scale))))

(defn property-overview-window [state property-type clicked-id-fn]
  (let [{:keys [skin]} state
        window (Window. "Edit" skin)]
    (doseq [row (property-overview-rows state property-type clicked-id-fn)]
      (doseq [cell row]
        (.add window ^Actor (:actor cell)))
      (.row window))
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    window))

(defn- scaled-image-button [texture-region scale]
  (ImageButton.
   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))

(defn- create-image-widget [state image]
  (scaled-image-button (textures/texture-region (:textures state) image) 2))

(defn- create-animation-widget [state animation]
  (let [table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 1))))]
    (doseq [image (:animation/frames animation)]
      (.add table ^Actor (scaled-image-button
                          (textures/texture-region (:textures state) image)
                          2)))
    (.row ^Table table)
    (.pack table)
    table))

(defn- boolean-widget-value [widget]
  (.isChecked ^CheckBox widget))

(defn- create-boolean-widget [state checked?]
  (doto (CheckBox. "" (:skin state))
    (.setChecked checked?)))

(defn- default-widget-value [widget]
  ((.getUserObject ^Actor widget) 1))

(defn- create-default-widget [state v]
  (Label. ^String (string/truncate (binding [*print-level* nil]
                                     (pr-str v))
                                   60)
          (:skin state)))

(defn- enum-widget-value [widget]
  (edn/read-string (.getSelected ^SelectBox widget)))

(defn- create-enum-widget [state schema v]
  (doto ^SelectBox (SelectBox. (:skin state))
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))

(defn- number-widget-value [widget]
  (edn/read-string (.getText ^TextField widget)))

(defn- create-number-widget [state schema v]
  (let [{:keys [skin]} state]
    (doto (TextField. ^String (pr-str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- string-widget-value [widget]
  (.getText ^TextField widget))

(defn- create-string-widget [state schema v]
  (let [{:keys [skin]} state]
    (doto (TextField. ^String (str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- val-max-widget-value [widget]
  (edn/read-string (.getText ^TextField widget)))

(defn- create-val-max-widget [state schema v]
  (let [{:keys [skin]} state]
    (doto (TextField. ^String (pr-str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))

(defn- play-sound-button [state sound-name]
  (let [{:keys [play-sound! skin]} state]
    (doto (TextButton. "play!" skin)
      (.addListener (proxy [ChangeListener] []
                      (changed [event _actor]
                        (play-sound! sound-name)))))))

(defn- sound-columns [state table sound-name open-select-sounds-handler]
  (let [{:keys [skin]} state]
    [{:actor (doto (TextButton. sound-name skin)
               (.addListener (proxy [ChangeListener] []
                               (changed [event _actor]
                                 (open-select-sounds-handler table)))))}
     {:actor (play-sound-button state sound-name)}]))

(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor]
    (.clearChildren ^Group table)
    (doseq [cell (->sound-columns table sound-name)]
      (.add ^Table table ^Actor (:actor cell)))
    (.row ^Table table)
    (.remove ^Actor (.firstAscendant ^Actor actor Window))
    (.pack ^Layout (.firstAscendant ^Actor table Window))
    (let [[k _] (.getUserObject ^Actor table)]
      (.setUserObject ^Actor table [k sound-name]))))

(defn- choose-sound-button [state table sound-name ->sound-columns]
  (let [{:keys [skin]} state]
    (doto (TextButton. sound-name skin)
      (.addListener (proxy [ChangeListener] []
                      (changed [event actor]
                        ((rebuild-sound-widget! table sound-name ->sound-columns) actor)))))))

(defn- list-sounds-table [pad rows]
  (let [^Table table (Table.)]
    (.pad (.defaults table) (float pad))
    (doseq [columns rows]
      (doseq [^Actor actor columns]
        (.add table actor))
      (.row table))
    (.pack table)
    table))

(defn- choose-sound-window [state table ->sound-columns]
  (let [{:keys [skin sound-names stage]} state
        ^Table list-table (list-sounds-table
                           5
                           (for [sound-name sound-names]
                             [(choose-sound-button state table sound-name ->sound-columns)
                              (play-sound-button state sound-name)]))
        window (Window. "Choose" skin)
        c (.add window (ScrollPane. list-table skin))]
    (.width c (float (+ (.getWidth list-table) 50)))
    (.height c (float (min (- (.getWorldHeight (.getViewport ^Stage stage)) 50)
                            (.getHeight list-table))))
    (.row window)
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    window))

(defn- open-select-sounds-handler [state table ->sound-columns]
  (fn []
    (.addActor ^Stage (:stage state) (choose-sound-window state table ->sound-columns))))

(defn- create-sound-widget [state sound-name]
  (let [{:keys [skin]} state
        table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (letfn [(sound-columns-fn [table sound-name]
              (sound-columns state table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler state table sound-columns-fn))]
      (doseq [cell (if sound-name
                    (sound-columns-fn table sound-name)
                    [{:actor
                      (doto (TextButton. "No sound" skin)
                        (.addListener (proxy [ChangeListener] []
                                        (changed [event _actor]
                                          ((open-select-fn table))))))}])]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table)
      table)))

(defn- one-to-one-widget-value [widget]
  (->> (.getChildren ^Group widget)
       (keep (fn [^Actor a] (.getUserObject a)))
       first))

(defn- add-one-to-one-rows [state table property-type property-id]
  (let [{:keys [db skin stage textures]} state
        redo-rows (fn [id]
                    (.clearChildren ^Group table)
                    (add-one-to-one-rows state table property-type id)
                    (.pack ^Layout (.firstAscendant ^Actor table Window)))]
    (doseq [row (cond-> []
                  (not property-id)
                  (conj [{:actor (doto (TextButton. "+" skin)
                                   (.addListener (proxy [ChangeListener] []
                                                   (changed [event _actor]
                                                     (.addActor ^Stage stage
                                                      ((:property-overview-window state)
                                                       state
                                                       property-type
                                                       (fn [actor id]
                                                         (.remove ^Actor (.firstAscendant ^Actor actor Window))
                                                         (redo-rows id))))))))}])
                  property-id
                  (conj [{:actor (let [property (db/get-raw @db property-id)]
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

(defn- create-one-to-one-widget [state [_ property-type] property-id]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-one-rows state table property-type property-id)
    table))

(defn- one-to-many-widget-value [widget]
  (->> (.getChildren ^Group widget)
       (keep (fn [^Actor a] (.getUserObject a)))
       set))

(defn- add-one-to-many-rows [state table property-type property-ids]
  (let [{:keys [db skin stage textures]} state
        redo-rows (fn [property-ids]
                    (.clearChildren ^Group table)
                    (add-one-to-many-rows state table property-type property-ids)
                    (.pack ^Layout (.firstAscendant ^Actor table Window)))]
    (doseq [row
     [[{:actor (doto (TextButton. "+" skin)
                 (.addListener (proxy [ChangeListener] []
                                 (changed [event _actor]
                                   (.addActor ^Stage stage
                                    ((:property-overview-window state)
                                     state
                                     property-type
                                     (fn [actor id]
                                       (.remove ^Actor (.firstAscendant ^Actor actor Window))
                                       (redo-rows (conj property-ids id)))))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw @db property-id)]
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

(defn- create-one-to-many-widget [state [_ property-type] property-ids]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-many-rows state table property-type property-ids)
    table))

(declare create-widget widget-value)

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

(defn map-widget-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? (fn [^Actor a] (.getUserObject a))) (.getChildren ^Group table))
              :let [[k _] (.getUserObject ^Actor widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defn- component-row-table
  [{:keys [display-remove-component-button?
           k
           state
           table]}]
  (let [{:keys [skin]} state
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
                                                       (@(:rebuild-editor-window! state))))))))
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
  (let [{:keys [schemas skin]} state
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
                                       (@(:rebuild-editor-window! state)))))))
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
  (let [{:keys [skin stage]} state
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

(defn- create-map-widget [state schema m build-widget widget-value]
  (let [schemas (:schemas state)]
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

(defn widget-value [[schema-k] widget schemas]
  (case schema-k
    :s/boolean (boolean-widget-value widget)
    :s/enum (enum-widget-value widget)
    :s/map (map-widget-value widget schemas)
    :s/number (number-widget-value widget)
    :s/one-to-many (one-to-many-widget-value widget)
    :s/one-to-one (one-to-one-widget-value widget)
    :s/string (string-widget-value widget)
    :s/val-max (val-max-widget-value widget)
    (default-widget-value widget)))

(defn- build-widget [state schema k v]
  (let [widget (create-widget state schema v)]
    (.setUserObject ^Actor widget [k v])
    widget))

(defn create-widget [state [schema-k :as schema] v]
  (case schema-k
    :s/animation (create-animation-widget state v)
    :s/boolean (create-boolean-widget state v)
    :s/enum (create-enum-widget state schema v)
    :s/image (create-image-widget state v)
    :s/map (create-map-widget state schema v
                              (fn [schema k v] (build-widget state schema k v))
                              widget-value)
    :s/number (create-number-widget state schema v)
    :s/one-to-many (create-one-to-many-widget state schema v)
    :s/one-to-one (create-one-to-one-widget state schema v)
    :s/sound (create-sound-widget state v)
    :s/string (create-string-widget state schema v)
    :s/val-max (create-val-max-widget state schema v)
    (create-default-widget state v)))

(defn- with-window-close [state f]
  (fn [actor]
    (try
     (swap! (:db state) f)
     (.remove ^Actor (.firstAscendant ^Actor actor Window))
     (catch Throwable t
       (binding [*print-level* 3]
         (pretty-repl/pretty-pst t 24))))))

(defn- property-editor-table [state widget on-save on-delete]
  (let [{:keys [skin]} state
        table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 5))))]
    (doto (.add table ^Actor widget)
      (.colspan (int 2)))
    (.row table)
    (doto (.add table ^Actor (doto (TextButton. "Save [LIGHT_GRAY](ENTER)[]" skin)
                               (.addListener (proxy [ChangeListener] []
                                               (changed [event actor]
                                                 (on-save actor))))))
      (.center))
    (doto (.add table ^Actor (doto (TextButton. "Delete" skin)
                               (.addListener (proxy [ChangeListener] []
                                               (changed [event actor]
                                                 (on-delete actor))))))
      (.center))
    (.row table)
    (.pack table)
    table))

(declare property-editor-window)

(defn- rebuild-editor-window! [state]
  (let [{:keys [schemas stage]} state
        window (-> (.getRoot ^Stage stage)
                   (.findActor "moon.ui.clojure.editor-window"))
        map-widget-table (.findActor ^Group window "moon.db.schema.map.ui.widget")
        property (map-widget-value map-widget-table schemas)]
    (.remove window)
    (.addActor ^Stage stage (property-editor-window state property))))

(defn property-editor-window [state property]
  (reset! (:rebuild-editor-window! state) #(rebuild-editor-window! state))
  (let [{:keys [properties-file schemas skin stage]} state
        schema (get schemas (keyword "properties" (namespace (:property/id property))))
        widget (create-widget state schema property)
        scroll-pane-height (.getWorldHeight (.getViewport ^Stage stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        on-delete (with-window-close state
                    (fn [db]
                      (assert (contains? db property-id))
                      (let [new-db (dissoc db property-id)]
                        (db/save! new-db properties-file)
                        new-db)))
        on-save (with-window-close state
                  (fn [db]
                    (let [property (get-widget-value)
                          id (:property/id property)]
                      (assert (contains? property :property/id))
                      (assert (contains? db id))
                      (let [schema (m/schema (schema/malli-form (get schemas (keyword "properties" (namespace (:property/id property)))) schemas))]
                        (when-not (m/validate schema property)
                          (throw (ex-info (str (me/humanize (m/explain schema property)))
                                          {:value property
                                           :schema (m/form schema)}))))
                      (let [new-db (assoc db id property)]
                        (db/save! new-db properties-file)
                        new-db))))
        ^Table table (property-editor-table state widget on-save on-delete)
        window (Window. "[SKY]Property[]" skin)]
    (.pad (.defaults window) (float 5))
    (let [c (.add window (ScrollPane. table skin))]
      (.width c (float (+ (.getWidth table) 50)))
      (.height c (float (min (- scroll-pane-height 50)
                             (.getHeight table)))))
    (.row window)
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    (.addActor window (proxy [Actor] []
                        (act [delta]
                          (when (.isKeyJustPressed Gdx/input Input$Keys/ENTER)
                            (on-save this))
                          (let [^Actor this this]
                            (proxy-super act delta)))
                        (draw [batch parent-alpha])))
    (.setName window "moon.ui.clojure.editor-window")
    window))

(defn main-window [state]
  (let [{:keys [schemas skin stage]} state
        window (Window. "Edit" skin)]
    (doseq [property-type (sort (filter #(= "properties" (namespace %)) (keys schemas)))]
      (.add window (doto (TextButton. (str/capitalize (name property-type)) skin)
                     (.addListener (proxy [ChangeListener] []
                                     (changed [event _actor]
                                       (.addActor ^Stage stage
                                                  (property-overview-window
                                                   state
                                                   property-type
                                                   (fn [_actor id]
                                                     (.addActor ^Stage stage
                                                                (property-editor-window state (db/get-raw @(:db state) id)))))))))))
      (.row window))
    (.pack window)
    window))

(defn listener []
  (let [sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "properties.edn")
        audio (atom nil)
        batch (atom nil)
        db (atom nil)
        rebuild-editor-window! (atom nil)
        skin (atom nil)
        stage (atom nil)
        textures (atom nil)
        play-sound! (fn [sound-name]
                      (let [sounds @audio]
                        (assert (contains? sounds sound-name) (str sound-name))
                        (.play ^Sound (get sounds sound-name))))]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! skin (Skin. (.internal Gdx/files "skin/uiskin.json")))
        (set! (.markupEnabled (.getData (.getFont ^Skin @skin "default-font"))) true)
        (reset! stage (Stage. (FitViewport. (float 1440) (float 900)) @batch))
        (reset! audio (into {}
                            (for [path sound-paths]
                              [path (.newSound Gdx/audio (.internal Gdx/files path))])))
        (reset! db (db/create schemas))
        (reset! textures (textures/create Gdx/files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}))
        (.setInputProcessor Gdx/input @stage)
        (.addActor ^Stage @stage
                   (main-window {:db db
                                 :play-sound! play-sound!
                                 :properties-file properties-file
                                 :property-overview-window property-overview-window
                                 :rebuild-editor-window! rebuild-editor-window!
                                 :schemas schemas
                                 :skin @skin
                                 :sound-names (keys @audio)
                                 :stage @stage
                                 :textures @textures})))

      (dispose [_]
        (run! Sound/.dispose (vals @audio))
        (Disposable/.dispose @batch)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures)))

      (render [_]
        (ScreenUtils/clear 0 0 0 0)
        (.act ^Stage @stage)
        (.draw ^Stage @stage))

      (resize [_ width height]
        (.update (.getViewport ^Stage @stage) width height true))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "!Editor!")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
