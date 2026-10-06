(ns moon.editor
  (:require [moon.db :as db]
            [moon.property :as property]
            [clojure.edn :as edn]
            [moon.coll :as coll]
            [moon.textures :as textures]
            [moon.audio :as audio]
            [moon.schemas :refer [default-value map-keys optional-keyset optional?]]
            [clojure.set :as set]
            [clojure.string :as str]
            [moon.throwable :as throwable]
            [moon.string :as string]
            [moon.error-window :as error-window]
            [moon.scene2d.table :as table]
            [moon.scene2d.window :as window]
            [moon.viewport :as viewport])
  (:import (com.badlogic.gdx ApplicationListener Files Gdx Graphics Input Input$Keys InputProcessor)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20)
           (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Image ImageButton Label ScrollPane SelectBox Skin Stack Table TextButton TextField TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Drawable Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(def state (atom nil))

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

(defn- play-sound-button [skin sound-name]
  (doto (TextButton. "play!" skin)
    (.addListener (proxy [ChangeListener] []
                    (changed [event _actor]
                      (audio/play! (:ctx/audio @state)
                                   sound-name))))))

(defn- sound-columns [skin table sound-name open-select-sounds-handler]
  [{:actor (doto (TextButton. sound-name skin)
             (.addListener (proxy [ChangeListener] []
                             (changed [event _actor]
                               ((open-select-sounds-handler table)
                                @state)))))}
   {:actor (play-sound-button skin sound-name)}])

(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (.clearChildren ^Group table)
    (table/add-rows! table [(->sound-columns skin table sound-name)])
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
    (.pack ^Layout (find-ancestor table (partial instance? window/class)))
    (let [[k _] (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table)]
      (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table [k sound-name]))))

(defn- choose-sound-button [skin table sound-name ->sound-columns]
  (doto (TextButton. sound-name skin)
    (.addListener (proxy [ChangeListener] []
                    (changed [event actor]
                      ((rebuild-sound-widget! table sound-name ->sound-columns) actor @state))))))

(defn- list-sounds-table [pad rows]
  (let [^Table table (Table.)]
    (.pad (.defaults table) (float pad))
    (doseq [columns rows]
      (doseq [^Actor actor columns]
        (.add table actor))
      (.row table))
    (.pack table)
    table))

(defn- choose-sound-window
  [{:keys [ctx/skin
           ctx/stage
           ctx/audio]}
   table
   ->sound-columns]
  (doto (window/create {:title "Choose"
                        :skin skin
                        :table/rows
                        [[(let [list-table (list-sounds-table
                                            5
                                            (for [sound-name (audio/names audio)]
                                              [(choose-sound-button skin table sound-name ->sound-columns)
                                               (play-sound-button skin sound-name)]))]
                            {:actor (ScrollPane. ^Actor list-table ^Skin skin)
                             :width  (+ (.getWidth ^com.badlogic.gdx.scenes.scene2d.Actor list-table) 50)
                             :height (min (- (viewport/get-world-height (.getViewport ^Stage stage)) 50)
                                          (.getHeight ^com.badlogic.gdx.scenes.scene2d.Actor list-table))})]]
                        :window/add-close-button? true})
    (window/set-modal! true)))

(defn- open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/stage]
        :as ctx}]
    (.addActor ^Stage stage (choose-sound-window ctx table ->sound-columns))))

(defn- overview-table-rows* [skin image-scale rows]
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
                                           (on-clicked actor @state))))
                         (.addListener (TextTooltip. ^String tooltip ^Skin skin)))
                       (doto (Label. ^String extra-info-text ^Skin skin)
                         (.setTouchable Touchable/disabled))])
                stack)})))

(defn- property-overview-rows
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (let [{:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw db property-type)
         (sort-by sort-by-fn)
         (map (fn [property]
                {:texture-region (textures/texture-region textures (property/image property))
                 :on-clicked (fn [actor ctx]
                               (clicked-id-fn actor (:property/id property) ctx))
                 :tooltip (property/tooltip property)
                 :extra-info-text (extra-info-text property)}))
         (partition-all columns)
         (overview-table-rows* skin image-scale))))

(defn- property-overview-window
  [{:keys [skin] :as opts}]
  (doto (window/create {:title "Edit"
                        :skin skin
                        :table/rows (property-overview-rows opts)
                        :window/add-close-button? true})
    (window/set-modal! true)))

(defn- with-window-close [f]
  (fn [actor {:keys [ctx/skin]
              :as ctx}]
    (try
     (reset! state (update ctx :ctx/db f))
     (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (.addActor ^Stage (:ctx/stage ctx)
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))

(defn- property-editor-table [rows]
  (doto (Table.)
    (table/set-cell-defaults! {:pad 5})
    (table/add-rows! rows)
    (.pack)))

(defn- property-editor-window
  [{:keys [db
           skin
           stage
           create-widget
           property]}]
  (let [schemas (:db/schemas db)
        schema (get schemas (property/type property))
        widget (create-widget schema property)
        scroll-pane-height (viewport/get-world-height (.getViewport ^Stage stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        table (property-editor-table
               [[{:actor widget :colspan 2}]
                [{:actor (doto (TextButton. "Save [LIGHT_GRAY](ENTER)[]" skin)
                           (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (clicked-save-fn actor @state)))))
                  :center? true}
                 {:actor (doto (TextButton. "Delete" skin)
                           (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (clicked-delete-fn actor @state)))))
                  :center? true}]])]
    (doto ^Group (window/create {:title "[SKY]Property[]"
                          :skin skin
                          :table/cell-defaults {:pad 5}
                          :table/rows [[{:actor (ScrollPane. ^Actor table ^Skin skin)
                                         :width (+ (.getWidth ^com.badlogic.gdx.scenes.scene2d.Actor table) 50)
                                         :height (min (- scroll-pane-height 50)
                                                      (.getHeight ^com.badlogic.gdx.scenes.scene2d.Actor table))}]]
                          :window/add-close-button? true})
      (window/set-modal! true)
      (.addActor (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
                    (act [delta]
                      (when (.isKeyJustPressed ^Input Gdx/input Input$Keys/ENTER)
                        (clicked-save-fn this @state))
                      (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                        (proxy-super act delta)))
                    (draw [batch parent-alpha])))
      (.setName "moon.ui.clojure.editor-window"))))

(defn- add-one-to-many-rows
  [db
   skin
   textures
   table
   property-type
   property-ids]
  (let [redo-rows (fn [db skin textures property-ids]
                    (.clearChildren ^Group table)
                    (add-one-to-many-rows db skin textures table property-type property-ids)
                    (.pack ^Layout (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[{:actor (doto (TextButton. "+" skin)
                 (.addListener (proxy [ChangeListener] []
                                 (changed [event _actor]
                                   (let [{:keys [ctx/db
                                                 ctx/skin
                                                 ctx/textures
                                                 ctx/stage]} @state]
                                     (.addActor ^Stage
                                      stage
                                      (property-overview-window
                                       {:db db
                                        :textures textures
                                        :skin skin
                                        :property-type property-type
                                        :clicked-id-fn (fn [actor id {:keys [ctx/db
                                                                             ctx/skin
                                                                             ctx/textures]}]
                                                         (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
                                                         (redo-rows db skin textures (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (property/image property)))
                    (.addListener (TextTooltip. ^String (property/tooltip property) ^Skin skin))
                    (.setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (TextButton. "-" skin)
                  (.addListener (proxy [ChangeListener] []
                                  (changed [event _actor]
                                    (let [{:keys [ctx/db
                                                  ctx/skin
                                                  ctx/textures]} @state]
                                      (redo-rows db skin textures
                                                 (disj property-ids id)))))))})])))

(defn- add-one-to-one-rows
  [db
   skin
   textures
   table
   property-type
   property-id]
  (let [redo-rows (fn [db skin textures id]
                    (.clearChildren ^Group table)
                    (add-one-to-one-rows db skin textures table property-type id)
                    (.pack ^Layout (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (doto (TextButton. "+" skin)
                   (.addListener (proxy [ChangeListener] []
                                   (changed [event _actor]
                                     (let [{:keys [ctx/db
                                                   ctx/skin
                                                   ctx/textures
                                                   ctx/stage]} @state]
                                       (.addActor ^Stage
                                        stage
                                        (property-overview-window
                                         {:db db
                                          :textures textures
                                          :skin skin
                                          :property-type property-type
                                          :clicked-id-fn (fn [actor id {:keys [ctx/db
                                                                               ctx/skin
                                                                               ctx/textures]}]
                                                           (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
                                                           (redo-rows db skin textures id))})))))))})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (property/image property)))
                     (.addListener (TextTooltip. ^String (property/tooltip property) ^Skin skin))
                     (.setUserObject property-id))}))]
      [(when property-id
         {:actor (doto (TextButton. "-" skin)
                   (.addListener (proxy [ChangeListener] []
                                   (changed [event _actor]
                                     (let [{:keys [ctx/db
                                                   ctx/skin
                                                   ctx/textures]} @state]
                                       (redo-rows db skin textures nil))))))})]])))

(defn- rebuild-editor-window!
  [{:keys [ctx/db
           ctx/skin
           ctx/stage]
    :as ctx}]
  (let [window (-> (.getRoot ^Stage stage)
                   (.findActor "moon.ui.clojure.editor-window"))
        map-widget-table (.findActor ^Group window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor window)
    (.addActor ^Stage stage
                      (property-editor-window
                       {:db db
                        :skin skin
                        :stage stage
                        :create-widget (fn [schema v] (create-widget schema v ctx))
                        :property property}))))

(defn- create-component-row
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (doto (Table.)
             (table/set-cell-defaults! {:pad 2})
             (table/add-rows! [[{:actor (when display-remove-component-button?
                                                  (doto (TextButton. "-" skin)
                                                    (.addListener (proxy [ChangeListener] []
                                                                    (changed [event _actor]
                                                                      (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (first (filter (fn [actor]
                                                                                                                                       (and (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor)
                                                                                                                                            (= k ((.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor) 0))))
                                                                                                                                     (.getChildren ^Group table))))
                                                                      (let [ctx @state]
                                                                        (rebuild-editor-window! ctx)))))))
                                         :left? true}
                                        {:actor (Label. ^String (k-label-text k) ^Skin skin)}]])
             (.pack))
    :right? true}
   {:actor nil
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])

(defn- add-component-window
  [{:keys [schemas schema map-widget-table skin build-widget]}]
  (let [window (doto (window/create {:title "Choose"
                                     :skin skin
                                     :table/cell-defaults {:pad 5}
                                     :window/add-close-button? true})
                 (window/set-modal! true))
        remaining-ks (sort (remove (set (keys (widget-value schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (TextButton. (name k) skin)
                  (.addListener (proxy [ChangeListener] []
                                  (changed [event _actor]
                                    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor window)
                                    (let [ctx @state]
                                      (table/add-rows! map-widget-table [(create-component-row
                                                                          {:skin skin
                                                                           :editor-widget (build-widget (get schemas k)
                                                                                                        k
                                                                                                        (default-value schemas k))
                                                                           :k k
                                                                           :display-remove-component-button? (optional? schemas schema k)
                                                                           :table map-widget-table})])
                                      (rebuild-editor-window! ctx))))))}]))
    (.pack ^Layout window)
    window))

(defn horiz-sep [colspan]
  (fn []
    [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
      :pad-top 2
      :pad-bottom 2
      :colspan colspan
      :fill-x? true
      :expand-x? true}]))

(defn- map-widget-table-create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?
           build-widget]}]
  (let [table (doto (Table.)
                (table/set-cell-defaults! {:pad 5})
                (.setName "moon.db.schema.map.ui.widget"))
        colspan 3
        component-rows (coll/interpose-f (horiz-sep colspan)
                                         (map (fn [k]
                                                (create-component-row
                                                 {:skin skin
                                                  :editor-widget (k->widget k)
                                                  :k k
                                                  :display-remove-component-button? (k->optional? k)
                                                  :table table}))
                                              ks-sorted))]
    (table/add-rows!
     table
     (concat [(when opt?
                [{:actor (doto (TextButton. "Add component" skin)
                           (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (let [{:keys [ctx/db
                                                           ctx/skin
                                                           ctx/stage]} @state]
                                               (.addActor ^Stage
                                                stage
                                                (add-component-window
                                                 {:skin skin
                                                  :schemas (:db/schemas db)
                                                  :schema schema
                                                  :map-widget-table table
                                                  :build-widget build-widget})))))))
                  :colspan colspan}])]
             [(when opt?
                [{:actor nil
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))

(defn- scaled-image-button [texture-region scale]
  (ImageButton.
   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))

(defn- default-widget [v skin]
  (Label. ^String (string/truncate (binding [*print-level* nil]
                                     (pr-str v))
                                   60)
          ^Skin skin))

(defn- animation-widget [animation textures]
  (doto (Table.)
    (table/set-cell-defaults! {:pad 1})
    (table/add-rows! [(for [image (:animation/frames animation)]
                        {:actor (scaled-image-button
                                 (textures/texture-region textures image)
                                 2)})])
    (.pack)))

(defn- boolean-widget [checked? skin]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defn- enum-widget [schema v skin]
  (doto ^SelectBox (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))

(defn- image-widget [image textures]
  (scaled-image-button (textures/texture-region textures image) 2))

(defn- map-widget [schema m db skin build-widget]
  (let [schemas (:db/schemas db)]
    (map-widget-table-create
     {:skin skin
      :schema schema
      :build-widget build-widget
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (coll/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defn- number-widget [schema v skin]
  (doto (TextField. ^String (pr-str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

(defn- one-to-many-widget [[_ property-type] property-ids db skin textures]
  (let [table (doto (Table.)
              (table/set-cell-defaults! {:pad 5}))]
    (add-one-to-many-rows db skin textures table property-type property-ids)
    table))

(defn- one-to-one-widget [[_ property-type] property-id db skin textures]
  (let [table (doto (Table.)
              (table/set-cell-defaults! {:pad 5}))]
    (add-one-to-one-rows db skin textures table property-type property-id)
    table))

(defn- sound-widget [sound-name skin]
  (let [table (doto (Table.)
              (table/set-cell-defaults! {:pad 5}))]
    (letfn [(sound-columns-fn [skin table sound-name]
              (sound-columns skin table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler table sound-columns-fn))]
      (table/add-rows! table [(if sound-name
                                (sound-columns-fn skin table sound-name)
                                [{:actor
                                  (doto (TextButton. "No sound" skin)
                                    (.addListener (proxy [ChangeListener] []
                                                    (changed [event _actor]
                                                      ((open-select-fn table)
                                                       @state)))))}])])
      table)))

(defn- string-widget [schema v skin]
  (doto (TextField. ^String (str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

(defn- val-max-widget [schema v skin]
  (doto (TextField. ^String (pr-str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

(defn- build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget [k v])
    widget))

(defn create-widget [[schema-k :as schema] v {:keys [ctx/db ctx/skin ctx/textures] :as ctx}]
  (case schema-k
    :s/animation (animation-widget v textures)
    :s/boolean (boolean-widget v skin)
    :s/enum (enum-widget schema v skin)
    :s/image (image-widget v textures)
    :s/map (map-widget schema v db skin (partial build-widget ctx))
    :s/number (number-widget schema v skin)
    :s/one-to-many (one-to-many-widget schema v db skin textures)
    :s/one-to-one (one-to-one-widget schema v db skin textures)
    :s/sound (sound-widget v skin)
    :s/string (string-widget schema v skin)
    :s/val-max (val-max-widget schema v skin)
    (default-widget v skin)))

(defn- main-window-f
  [{:keys [ctx/db
           ctx/skin]}]
  (doto (window/create {:title "Edit"
                        :skin skin
                        :table/rows (for [property-type (sort (db/property-types db))]
                                      [{:actor (doto (TextButton. (str/capitalize (name property-type)) skin)
                                                     (.addListener (proxy [ChangeListener] []
                                                                           (changed [event _actor]
                                                                             (let [{:keys [ctx/db
                                                                                           ctx/skin
                                                                                           ctx/textures
                                                                                           ctx/stage]
                                                                                    :as ctx} @state]
                                                                               (.addActor ^Stage stage
                                                                                                 (property-overview-window
                                                                                                  {:db db
                                                                                                   :textures textures
                                                                                                   :skin skin
                                                                                                   :property-type property-type
                                                                                                   :clicked-id-fn (fn [_actor id {:keys [ctx/db
                                                                                                                                         ctx/skin
                                                                                                                                         ctx/stage]
                                                                                                                                  :as ctx}]
                                                                                                                    (.addActor ^Stage stage
                                                                                                                                      (property-editor-window
                                                                                                                                       {:db db
                                                                                                                                        :skin skin
                                                                                                                                        :stage stage
                                                                                                                                        :create-widget (fn [schema v]
                                                                                                                                                         (create-widget schema v ctx))
                                                                                                                                        :property (db/get-raw db id)})))})))))))}])})))

(defn listener []
  (reify ApplicationListener
    (create [_]
      (reset! state
              (let [batch (SpriteBatch.)
                    skin (Skin. ^FileHandle (.internal ^Files Gdx/files "skin/uiskin.json"))
                    _ (set! (.markupEnabled ^BitmapFont$BitmapFontData
                                            (.getData (.getFont ^Skin skin "default-font")))
                            true)
                    stage* (Stage. (FitViewport. (float 1440) (float 900)) batch)
                    _ (.setInputProcessor ^Input Gdx/input ^InputProcessor stage*)
                    ctx {:ctx/audio (audio/create Gdx/audio Gdx/files)
                         :ctx/batch batch
                         :ctx/skin skin
                         :ctx/db (db/create)
                         :ctx/stage stage*
                         :ctx/textures (textures/create Gdx/files {:folder "resources/"
                                                                   :extensions #{"png" "bmp"}})}]
                (.addActor ^Stage (:ctx/stage ctx) (main-window-f ctx))
                ctx)))
    (dispose [_]
      (let [{:keys [ctx/audio
                    ctx/skin
                    ctx/batch
                    ctx/textures]} @state]
        (audio/dispose! audio)
        (Disposable/.dispose batch)
        (Disposable/.dispose skin)
        (run! Disposable/.dispose (vals textures))))
    (render [_]
      (let [stage (:ctx/stage @state)
            gl (.getGL20 ^Graphics Gdx/graphics)]
        (.glClearColor ^GL20 gl 0 0 0 0)
        (.glClear ^GL20 gl GL20/GL_COLOR_BUFFER_BIT)
        (.act ^Stage stage)
        (.draw ^Stage stage)))
    (resize [_ width height]
      (viewport/update! (.getViewport ^Stage (:ctx/stage @state)) width height true))
    (pause [_])
    (resume [_])))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "!Editor!")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
