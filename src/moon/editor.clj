(ns moon.editor
  (:require [moon.db :as db]
            [moon.property :as property]
            [clojure.edn :as edn]
            [moon.coll :as coll]
            [moon.textures :as textures]
            [moon.audio :as audio]
            [gdx.stage :as stage]
            [moon.schemas :refer [default-value map-keys optional-keyset optional?]]
            [clojure.set :as set]
            [clojure.string :as str]
            [moon.throwable :as throwable]
            [moon.string :as string]
            [moon.error-window :as error-window]
            [moon.scene2d.table :as table]
            [moon.scene2d.window :as window]
            [gdx.input :as input]
            [moon.scene2d.group :as group]
            [moon.viewport :as viewport])
  (:import (com.badlogic.gdx Application ApplicationListener Files Gdx Graphics)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20)
           (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Event Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Image ImageButton Label ScrollPane SelectBox Skin Stack TextButton TextField TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Drawable Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn- find-ancestor [a pred?]
  (loop [actor a]
    (if-let [p (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))


(defn k-label-text [k]
  (name k) ;(str "[GRAY]:" (namespace k) "[]/" (name k))
  )

(def state (atom nil))

(defn- get-stage [ctx]
  (:ctx/stage ctx))

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

(defmulti create-widget
  (fn [[schema-k :as _schema] v ctx]
    schema-k))

(defmulti widget-value
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))

(defn- map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a))) (group/get-children table))
              :let [[k _] (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defmethod widget-value :default
  [_ widget _schemas]
  ((.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget) 1))

(defmethod widget-value :s/boolean
  [_ widget _schemas]
  (.isChecked ^CheckBox widget))

(defmethod widget-value :s/enum
  [_ widget _schemas]
  (edn/read-string (.getSelected ^SelectBox widget)))

(defmethod widget-value :s/map
  [_ table schemas]
  (map-widget-table-get-value table schemas))

(defmethod widget-value :s/number
  [_ widget _schemas]
  (edn/read-string (.getText ^TextField widget)))

(defmethod widget-value :s/one-to-many
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a)))
       set))

(defmethod widget-value :s/one-to-one
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep (fn [^com.badlogic.gdx.scenes.scene2d.Actor a] (.getUserObject a)))
       first))

(defmethod widget-value :s/string
  [_ widget _schemas]
  (.getText ^TextField widget))

(defmethod widget-value :s/val-max
  [_ widget _schemas]
  (edn/read-string (.getText ^TextField widget)))

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

(defn- build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor widget [k v])
    widget))

(defn- sound-columns [skin table sound-name open-select-sounds-handler]
  [{:actor (doto (TextButton. sound-name skin)
             (.addListener (proxy [ChangeListener] []
                                      (changed [event _actor]
                                        ((open-select-sounds-handler table)
                                         (:stage/ctx (.getStage ^Event event)))))))}
   {:actor (doto (TextButton. "play!" skin)
             (.addListener (proxy [ChangeListener] []
                                      (changed [event _actor]
                                        (audio/play! (:ctx/audio (:stage/ctx (.getStage ^Event event)))
                                                     sound-name)))))}])

(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(->sound-columns skin table sound-name)])
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
    (.pack ^Layout (find-ancestor table (partial instance? window/class)))
    (let [[k _] (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table)]
      (.setUserObject ^com.badlogic.gdx.scenes.scene2d.Actor table [k sound-name]))))

(defn- open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin]
        :as ctx}]
    (let [stage (get-stage ctx)]
      (stage/add-actor! stage
                      (doto (window/create {:title "Choose"
                                            :skin skin
                                            :table/rows
                                            [[(let [table (table/create {:table/cell-defaults {:pad 5}
                                                                         :table/rows (for [sound-name (audio/names (:ctx/audio ctx))]
                                                                                       [{:actor (doto (TextButton. sound-name skin)
                                                                                                      (.addListener (proxy [ChangeListener] []
                                                                                                                           (changed [event actor]
                                                                                                                             ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (.getStage ^Event event)))))))}
                                                                                        {:actor (doto (TextButton. "play!" skin)
                                                                                                      (.addListener (proxy [ChangeListener] []
                                                                                                                           (changed [event _actor]
                                                                                                                             (audio/play! (:ctx/audio (:stage/ctx (.getStage ^Event event)))
                                                                                                                                          sound-name)))))}])})]
                                               {:actor (ScrollPane. ^Actor table ^Skin skin)
                                                :width  (+ (.getWidth ^com.badlogic.gdx.scenes.scene2d.Actor table) 50)
                                                :height (min (- (viewport/get-world-height (:stage/viewport stage)) 50)
                                                             (.getHeight ^com.badlogic.gdx.scenes.scene2d.Actor table))})]]
                                            :window/add-close-button? true})
                            (window/set-modal! true))))))

(defn- overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (Stack.)]
                (run! #(group/add-actor! stack %)
                      [(doto (ImageButton.
                              (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                (.setMinSize (* image-scale (.getRegionWidth ^TextureRegion texture-region))
                                             (* image-scale (.getRegionHeight ^TextureRegion texture-region)))))
                        (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (on-clicked actor (:stage/ctx (.getStage ^Event event))))))
                        (.addListener (TextTooltip. ^String tooltip ^Skin skin)))
                       (doto (Label. ^String extra-info-text ^Skin skin)
                         (.setTouchable Touchable/disabled))])
                stack)})))

(defn- property-overview-window
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (doto (window/create {:title "Edit"
                        :skin skin
                        :table/rows (let [{:keys [sort-by-fn
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
                                           (overview-table-rows* skin image-scale)))
                        :window/add-close-button? true})
    (window/set-modal! true)))

(defn- with-window-close [f]
  (fn [actor {:keys [ctx/skin]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           gdx-stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor actor)]
       (stage/set-ctx! gdx-stage new-ctx))
     (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! (get-stage ctx)
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))

(defn- property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin]} ctx
        stage (get-stage ctx)
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        widget (create-widget schema property ctx)
        scroll-pane-height (viewport/get-world-height (:stage/viewport stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (TextButton. "Save [LIGHT_GRAY](ENTER)[]" skin)
                                     (.addListener (proxy [ChangeListener] []
                                                              (changed [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (.getStage ^Event event)))))))
                            :center? true}
                           {:actor (doto (TextButton. "Delete" skin)
                                     (.addListener (proxy [ChangeListener] []
                                                              (changed [event actor]
                                                                (clicked-delete-fn actor (:stage/ctx (.getStage ^Event event)))))))
                            :center? true}]]]
    (doto (window/create {:title "[SKY]Property[]"
                          :skin skin
                          :table/cell-defaults {:pad 5}
                          :table/rows [[(let [table (table/create {:table/cell-defaults {:pad 5}
                                                                  :table/rows scroll-pane-rows})]
                                          {:actor (ScrollPane. ^Actor table ^Skin skin)
                                           :width (+ (.getWidth ^com.badlogic.gdx.scenes.scene2d.Actor table) 50)
                                           :height (min (- scroll-pane-height 50)
                                                        (.getHeight ^com.badlogic.gdx.scenes.scene2d.Actor table))})]]
                          :window/add-close-button? true})
      (window/set-modal! true)
      (group/add-actor! (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
                          (act [delta]
                            (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                              (let [ctx (:stage/ctx stage)]
                                (when (input/key-just-pressed? (:ctx/input ctx)
                                                              :input.keys/enter)
                                  (clicked-save-fn this ctx))))
                            (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                              (proxy-super act delta)))
                          (draw [batch parent-alpha])))
      (.setName "moon.ui.clojure.editor-window"))))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (.pack ^Layout (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[{:actor (doto (TextButton. "+" skin)
                 (.addListener (proxy [ChangeListener] []
                                          (changed [event _actor]
                                            (let [{:keys [ctx/db
                                                          ctx/skin
                                                          ctx/textures]
                                                   :as ctx} (:stage/ctx (.getStage ^Event event))]
                                              (stage/add-actor!
                                               (get-stage ctx)
                                               (property-overview-window
                                                {:db db
                                                 :textures textures
                                                 :skin skin
                                                 :property-type property-type
                                                 :clicked-id-fn (fn [actor id ctx]
                                                                  (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
                                                                  (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (property/image property)))
                    (.addListener (TextTooltip. ^String (property/tooltip property) ^Skin skin))
                    (.setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (TextButton. "-" skin)
                  (.addListener (proxy [ChangeListener] []
                                           (changed [event _actor]
                                             (redo-rows (:stage/ctx (.getStage ^Event event))
                                                        (disj property-ids id))))))})])))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (.pack ^Layout (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (doto (TextButton. "+" skin)
                   (.addListener (proxy [ChangeListener] []
                                            (changed [event _actor]
                                              (let [{:keys [ctx/db
                                                            ctx/skin
                                                            ctx/textures]
                                                     :as ctx} (:stage/ctx (.getStage ^Event event))]
                                                (stage/add-actor!
                                                 (get-stage ctx)
                                                 (property-overview-window
                                                  {:db db
                                                   :textures textures
                                                   :skin skin
                                                   :property-type property-type
                                                   :clicked-id-fn (fn [actor id ctx]
                                                                    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-ancestor actor (partial instance? window/class)))
                                                                    (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (property/image property)))
                     (.addListener (TextTooltip. ^String (property/tooltip property) ^Skin skin))
                     (.setUserObject property-id))}))]
      [(when property-id
         {:actor (doto (TextButton. "-" skin)
                   (.addListener (proxy [ChangeListener] []
                                            (changed [event _actor]
                                              (redo-rows (:stage/ctx (.getStage ^Event event))
                                                         nil)))))})]])))

(defn- rebuild-editor-window!
  [{:keys [ctx/db]
    :as ctx}]
  (let [stage (get-stage ctx)
        window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.clojure.editor-window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor window)
    (stage/add-actor! stage
                      (property-editor-window
                       {:ctx ctx
                        :property property}))))

(defn- create-component-row
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (table/create {:table/cell-defaults {:pad 2}
                          :table/rows [[{:actor (when display-remove-component-button?
                                     (doto (TextButton. "-" skin)
                                       (.addListener (proxy [ChangeListener] []
                                                                (changed [event _actor]
                                                                  (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (first (filter (fn [actor]
                                                                                                          (and (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor)
                                                                                                               (= k ((.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor actor) 0))))
                                                                                                        (group/get-children table))))
                                                                  (let [ctx (:stage/ctx (.getStage ^Event event))]
                                                                    (rebuild-editor-window! ctx)))))))
                            :left? true}
                           {:actor (Label. ^String (k-label-text k) ^Skin skin)}]]})
    :right? true}
   {:actor nil
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])

(defn- add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
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
                                             (let [ctx (:stage/ctx (.getStage ^Event event))]
                                               (table/add-rows! map-widget-table [(create-component-row
                                                                            {:skin skin
                                                                             :editor-widget (build-widget ctx
                                                                                                          (get schemas k)
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
           opt?]}]
  (let [table (doto (table/create {:table/cell-defaults {:pad 5}})
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
                                                                    ctx/skin]
                                                             :as ctx} (:stage/ctx (.getStage ^Event event))]
                                                        (stage/add-actor!
                                                         (get-stage ctx)
                                                         (add-component-window
                                                          {:skin skin
                                                           :schemas (:db/schemas db)
                                                           :schema schema
                                                           :map-widget-table table})))))))
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

(defmethod create-widget :default
  [_ v {:keys [ctx/skin]}]
  (Label. ^String (string/truncate (binding [*print-level* nil]
                         (pr-str v))
                       60)
             ^Skin skin))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create {:table/cell-defaults {:pad 1}
                :table/rows [(for [image (:animation/frames animation)]
                               {:actor
                                (let [scale 2
                                      texture-region (textures/texture-region textures image)]
                                  (ImageButton.
                                   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))})]}))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (doto ^SelectBox (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))

(defmethod create-widget :s/image
  [_ image {:keys [ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (ImageButton.
     (doto (TextureRegionDrawable. ^TextureRegion texture-region)
       (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                    (* scale (.getRegionHeight ^TextureRegion texture-region)))))))

(defmethod create-widget :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (map-widget-table-create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (coll/sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod create-widget :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (TextField. ^String (pr-str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

(defmethod create-widget :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (table/create {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod create-widget :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (table/create {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod create-widget :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (table/create {:table/cell-defaults {:pad 5}})]
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
                                                      (:stage/ctx (.getStage ^Event event)))))))}])])
      table)))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (TextField. ^String (str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

(defmethod create-widget :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (TextField. ^String (pr-str v) ^Skin skin)
    (.addListener (TextTooltip. ^String (str schema) ^Skin skin))))

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
                                                                                           ctx/textures]
                                                                                    :as ctx} (:stage/ctx (.getStage ^Event event))]
                                                                               (stage/add-actor! (get-stage ctx)
                                                                                                 (property-overview-window
                                                                                                  {:db db
                                                                                                   :textures textures
                                                                                                   :skin skin
                                                                                                   :property-type property-type
                                                                                                   :clicked-id-fn (fn [_actor id ctx]
                                                                                                                    (stage/add-actor! (get-stage ctx)
                                                                                                                                        (property-editor-window
                                                                                                                                         {:ctx ctx
                                                                                                                                          :property (db/get-raw db id)})))})))))))}])})))

(defn- input-f [ctx]
  (assoc ctx :ctx/input (.getInput ^Application Gdx/app)))

(defn- audio-f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/audio (audio/create (.getAudio ^Application Gdx/app) files)))

(defn- files-f [ctx]
  (assoc ctx :ctx/files (.getFiles ^Application Gdx/app)))

(defn- batch-f [ctx]
  (assoc ctx :ctx/batch (SpriteBatch.)))

(defn- skin-f [{:keys [ctx/files] :as ctx}]
  (let [skin (Skin. ^FileHandle (.internal ^Files files "skin/uiskin.json"))]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData
                          (.getData (.getFont ^Skin skin "default-font")))
          true)
    (assoc ctx :ctx/skin skin)))

(defn- db-f [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn- stage-f [{:keys [ctx/input
                        ctx/batch] :as ctx}]
  (let [stage* (stage/create (FitViewport. (float 1440) (float 900)) batch)]
    (input/set-processor! input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/add-actor! (get-stage ctx) (main-window-f ctx))
      ctx)))

(defn- textures-f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (textures/create files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})))

(defn create []
  (-> {}
      input-f
      files-f
      audio-f
      batch-f
      skin-f
      db-f
      stage-f
      textures-f))

(defn dispose [{:keys [ctx/audio
                       ctx/skin
                       ctx/batch
                       ctx/textures]}]
  (audio/dispose! audio)
  (Disposable/.dispose batch)
  (Disposable/.dispose skin)
  (run! Disposable/.dispose (vals textures)))

(defn render [ctx]
  (let [stage (get-stage ctx)
        gl (.getGL20 ^Graphics Gdx/graphics)
        _ (.glClearColor ^GL20 gl 0 0 0 0)
        _ (.glClear ^GL20 gl GL20/GL_COLOR_BUFFER_BIT)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize [ctx width height]
  (viewport/update! (:stage/viewport (get-stage ctx)) width height true))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application.
    (reify ApplicationListener
      (create [_]
        (reset! state (create)))
      (dispose [_]
        (dispose @state))
      (render [_]
        (swap! state render))
      (resize [_ width height]
        (resize @state width height))
      (pause [_])
      (resume [_]))
    (doto (Lwjgl3ApplicationConfiguration.)
      (.setTitle "!Editor!")
      (.setWindowedMode 1440 900)
      (.setForegroundFPS 60))))
