(ns editor.listener
  (:require [moon.db :as db]
            [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [moon.coll :as coll]
            [clojure.java.io :as io]
            [moon.textures :as textures]
            [moon.schema :as schema]
            [moon.map-schema :as map-schema]
            [clojure.set :as set]
            [clojure.string :as str]
            [clj-commons.pretty.repl :as pretty-repl]
            [moon.string :as string])
  (:import (com.badlogic.gdx ApplicationListener Audio Files Gdx Input Input$Keys)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.graphics.g2d SpriteBatch TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Image ImageButton Label ScrollPane SelectBox Skin Stack Table TextButton TextField TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn- find-ancestor [a pred?]
  (loop [actor a]
    (if-let [p (Actor/.getParent actor)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))

(defn k-label-text [k]
  (name k) ;(str "[GRAY]:" (namespace k) "[]/" (name k))
  )

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

(declare create-widget widget-value)

(defn- map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a))) (.getChildren ^Group table))
              :let [[k _] (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defn widget-value [[schema-k] widget schemas]
  (case schema-k
    :s/boolean (.isChecked ^CheckBox widget)
    :s/enum (edn/read-string (.getSelected ^SelectBox widget))
    :s/map (map-widget-table-get-value widget schemas)
    :s/number (edn/read-string (.getText ^TextField widget))
    :s/one-to-many (->> (.getChildren ^Group widget)
                        (keep (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a)))
                        set)
    :s/one-to-one (->> (.getChildren ^Group widget)
                       (keep (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a)))
                       first)
    :s/string (.getText ^TextField widget)
    :s/val-max (edn/read-string (.getText ^TextField widget))
    ((.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget) 1)))

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

(defn- play-sound-button [state sound-name]
  (let [{:keys [play-sound! skin]} @state]
    (doto (TextButton. "play!" skin)
      (.addListener (proxy [ChangeListener] []
                      (changed [event _actor]
                        (play-sound! sound-name)))))))

(defn- sound-columns [state table sound-name open-select-sounds-handler]
  (let [{:keys [skin]} @state]
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
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? Window)))
    (.pack ^Layout (find-ancestor table (partial instance? Window)))
    (let [[k _] (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table)]
      (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table [k sound-name]))))

(defn- choose-sound-button [state table sound-name ->sound-columns]
  (let [{:keys [skin]} @state]
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
  (let [{:keys [skin sound-names stage]} @state
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
    (.addActor ^Stage (:stage @state) (choose-sound-window state table ->sound-columns))))

(defn- overview-table-rows* [state image-scale rows]
  (let [{:keys [skin]} @state]
    (for [row rows]
      (for [{:keys [texture-region
                    on-clicked
                    tooltip
                    extra-info-text]} row]
        {:actor (let [stack (Stack.)]
                  (run! #(.addActor ^Group stack ^Actor %)
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
  (let [{:keys [db textures]} @state
        {:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw db property-type)
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

(defn- property-overview-window [state property-type clicked-id-fn]
  (let [{:keys [skin]} @state
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

(defn- with-window-close [state f]
  (fn [actor]
    (try
     (swap! state update :db f)
     (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? Window)))
     (catch Throwable t
       (binding [*print-level* 3]
         (pretty-repl/pretty-pst t 24))))))

(defn- property-editor-table [state widget on-save on-delete]
  (let [{:keys [skin]} @state
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

(defn- property-editor-window [state property]
  (let [{:keys [db skin stage]} @state
        schemas (:db/schemas db)
        schema (get schemas (keyword "properties" (namespace (:property/id property))))
        widget (create-widget state schema property)
        scroll-pane-height (.getWorldHeight (.getViewport ^Stage stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        on-delete (with-window-close state (fn [db]
                                             (db/delete! db property-id)))
        on-save (with-window-close state (fn [db]
                                           (db/update! db (get-widget-value))))
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
                                       (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? Window)))
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
                                                         (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? Window)))
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

(defn- rebuild-editor-window! [state]
  (let [{:keys [db stage]} @state
        window (-> (.getRoot ^Stage stage)
                   (.findActor "moon.ui.clojure.editor-window"))
        map-widget-table (.findActor ^Group window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (.remove window)
    (.addActor ^Stage stage (property-editor-window state property))))

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
                                                       (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (first (filter (fn [actor]
                                                                                                                        (and (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor)
                                                                                                                             (= k ((.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor) 0))))
                                                                                                                      (.getChildren ^Group table))))
                                                       (rebuild-editor-window! state)))))))
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
                                       (rebuild-editor-window! state))))))
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

(defn- sound-widget [state sound-name]
  (let [{:keys [skin]} @state
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
    (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget [k v])
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

(defn- main-window [state]
  (let [{:keys [db skin stage]} @state
        window (Window. "Edit" skin)]
    (doseq [property-type (sort (db/property-types db))]
      (.add window (doto (TextButton. (str/capitalize (name property-type)) skin)
                     (.addListener (proxy [ChangeListener] []
                                     (changed [event _actor]
                                       (.addActor ^Stage stage
                                                  (property-overview-window
                                                   state
                                                   property-type
                                                   (fn [_actor id]
                                                     (.addActor ^Stage stage
                                                                (property-editor-window state (db/get-raw (:db @state) id)))))))))))
      (.row window))
    (.pack window)
    window))

(defn listener []
  (let [state (atom nil)
        play-sound! (fn [sound-name]
                      (let [audio (:audio @state)]
                        (assert (contains? audio sound-name) (str sound-name))
                        (.play ^Sound (get audio sound-name))))]
    (reify ApplicationListener
      (create [_]
        (let [batch (SpriteBatch.)
              skin (Skin. (.internal Gdx/files "skin/uiskin.json"))
              _ (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
              stage (Stage. (FitViewport. (float 1440) (float 900)) batch)
              audio (into {}
                          (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                :let [path (format "sounds/%s.wav" sound-name)]]
                            [sound-name
                             (.newSound ^Audio Gdx/audio (.internal ^Files Gdx/files path))]))
              db (db/create)
              textures (textures/create Gdx/files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})]
          (reset! state {:audio audio
                         :batch batch
                         :db db
                         :play-sound! play-sound!
                         :skin skin
                         :sound-names (keys audio)
                         :stage stage
                         :textures textures})
          (.setInputProcessor Gdx/input stage)
          (.addActor stage (main-window state))))
      (dispose [_]
        (let [{:keys [audio batch skin textures]} @state]
          (run! Disposable/.dispose (vals audio))
          (Disposable/.dispose batch)
          (Disposable/.dispose skin)
          (run! Disposable/.dispose (vals textures))))
      (render [_]
        (let [{:keys [stage]} @state]
          (ScreenUtils/clear 0 0 0 0)
          (.act ^Stage stage)
          (.draw ^Stage stage)))
      (resize [_ width height]
        (.update (.getViewport ^Stage (:stage @state)) width height true))
      (pause [_])
      (resume [_]))))
