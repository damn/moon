(ns moon.editor
  (:require [clojure.gdx :as gdx]
            [moon.db :as db]
            [moon.property :as property]
            [clojure.edn :as edn]
            [moon.coll :as coll]
            [moon.schema.register-methods]
            [moon.textures :as textures]
            [moon.audio :as audio]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [clojure.gdx.scenes.scene2d.actor :as actor :refer [find-ancestor]]
            [moon.schemas :refer [default-value map-keys optional-keyset optional?]]
            [clojure.set :as set]
            [clojure.string :as str]
            [moon.throwable :as throwable]
            [moon.string :as string]
            [moon.error-window :as error-window]
            [clojure.gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scenes.scene2d.ui.window :refer [add-close-button!]]
            [clojure.gdx.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [clojure.gdx.input :as input]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [clojure.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as ui-skin]
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [clojure.gdx.application :as application]
            [clojure.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [clojure.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(defn k-label-text [k]
  (name k) ;(str "[GRAY]:" (namespace k) "[]/" (name k))
  )

(def state (atom nil))

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
        (for [widget (filter (comp vector? actor/get-user-object) (group/get-children table))
              :let [[k _] (actor/get-user-object widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defmethod widget-value :default
  [_ widget _schemas]
  ((actor/get-user-object widget) 1))

(defmethod widget-value :s/boolean
  [_ widget _schemas]
  (check-box/isChecked widget))

(defmethod widget-value :s/enum
  [_ widget _schemas]
  (edn/read-string (select-box/getSelected widget)))

(defmethod widget-value :s/map
  [_ table schemas]
  (map-widget-table-get-value table schemas))

(defmethod widget-value :s/number
  [_ widget _schemas]
  (edn/read-string (text-field/getText widget)))

(defmethod widget-value :s/one-to-many
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep actor/get-user-object)
       set))

(defmethod widget-value :s/one-to-one
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep actor/get-user-object)
       first))

(defmethod widget-value :s/string
  [_ widget _schemas]
  (text-field/getText widget))

(defmethod widget-value :s/val-max
  [_ widget _schemas]
  (edn/read-string (text-field/getText widget)))

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
   :skill/cost])

(defn- build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (actor/set-user-object! widget [k v])
    widget))

(defn- sound-columns [skin table sound-name open-select-sounds-handler]
  [{:actor (doto (text-button/create sound-name skin)
             (actor/add-listener! (change-listener/create
                                      (fn [event _actor]
                                        ((open-select-sounds-handler table)
                                         (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create "play!" skin)
             (actor/add-listener! (change-listener/create
                                      (fn [event _actor]
                                        (audio/play! (:ctx/audio (:stage/ctx (event/get-stage event)))
                                                     sound-name)))))}])

(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(->sound-columns skin table sound-name)])
    (actor/remove! (find-ancestor actor (partial instance? window/class)))
    (layout/pack (find-ancestor table (partial instance? window/class)))
    (let [[k _] (actor/get-user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (doto (doto (window/new "Choose" skin)
    (table/set-opts! {:title "Choose"
                              :skin skin
                              :table/rows
                              [[(let [table (table/create {:table/cell-defaults {:pad 5}
                                                            :table/rows (for [sound-name (audio/names (:ctx/audio ctx))]
                                                                          [{:actor (doto (text-button/create sound-name skin)
                                                                                         (actor/add-listener! (change-listener/create
                                                                                                              (fn [event actor]
                                                                                                                ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/get-stage event)))))))}
                                                                           {:actor (doto (text-button/create "play!" skin)
                                                                                         (actor/add-listener! (change-listener/create
                                                                                                              (fn [event _actor]
                                                                                                                (audio/play! (:ctx/audio (:stage/ctx (event/get-stage event)))
                                                                                                                             sound-name)))))}])})]
                                {:actor (scroll-pane/new table skin)
                                 :width  (+ (actor/get-width table) 50)
                                 :height (min (- (viewport/getWorldHeight (:stage/viewport stage)) 50)
                                              (actor/get-height table))})]]}))
                            (add-close-button! skin)
                            (window/setModal true)))))

(defn- overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/new)]
                (run! #(group/add-actor! stack %)
                      [(doto (image-button/new
                              (doto (texture-region-drawable/new texture-region)
                                (texture-region-drawable/setMinSize (* image-scale (texture-region/getRegionWidth texture-region))
                                                (* image-scale (texture-region/getRegionHeight texture-region)))))
                        (actor/add-listener! (change-listener/create
                                           (fn [event actor]
                                             (on-clicked actor (:stage/ctx (event/get-stage event))))))
                        (actor/add-listener! (text-tooltip/new tooltip skin)))
                       (doto (label/create extra-info-text skin)
                         (actor/set-touchable! touchable/disabled))])
                stack)})))

(defn- property-overview-window
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (doto (doto (window/new "Edit" skin)
    (table/set-opts! {:title "Edit"
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
                             (overview-table-rows* skin image-scale)))}))
    (add-close-button! skin)
    (window/setModal true)))

(defn- with-window-close [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (actor/get-stage actor)]
       (stage/set-ctx! stage new-ctx))
     (actor/remove! (find-ancestor actor (partial instance? window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! stage
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))

(defn- property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        widget (create-widget schema property ctx)
        scroll-pane-height (viewport/getWorldHeight (:stage/viewport stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create "Save [LIGHT_GRAY](ENTER)[]" skin)
                                     (actor/add-listener! (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create "Delete" skin)
                                     (actor/add-listener! (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-delete-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}]]]
    (doto (doto (window/new "[SKY]Property[]" skin)
    (table/set-opts! {:title "[SKY]Property[]"
            :skin skin
            :table/cell-defaults {:pad 5}
            :table/rows [[(let [table (table/create {:table/cell-defaults {:pad 5}
                                                   :table/rows scroll-pane-rows})]
                            {:actor (scroll-pane/new table skin)
                             :width (+ (actor/get-width table) 50)
                             :height (min (- scroll-pane-height 50)
                                          (actor/get-height table))})]]}))
      (add-close-button! skin)
      (window/setModal true)
      (group/add-actor! (actor/new
                       (fn [this _delta]
                         (when-let [stage (actor/get-stage this)]
                           (let [ctx (:stage/ctx stage)]
                             (when (input/key-just-pressed? (:ctx/input ctx)
                                                           :input.keys/enter)
                               (clicked-save-fn this ctx)))))
                       (fn [_actor _batch _parent-alpha])))
      (actor/set-name! "moon.ui.clojure.editor-window"))))

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
                    (layout/pack (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[{:actor (doto (text-button/create "+" skin)
                 (actor/add-listener! (change-listener/create
                                          (fn [event _actor]
                                            (let [{:keys [ctx/db
                                                          ctx/skin
                                                          ctx/stage
                                                          ctx/textures]
                                                   :as ctx} (:stage/ctx (event/get-stage event))]
                                              (stage/add-actor!
                                               stage
                                               (property-overview-window
                                                {:db db
                                                 :textures textures
                                                 :skin skin
                                                 :property-type property-type
                                                 :clicked-id-fn (fn [actor id ctx]
                                                                  (actor/remove! (find-ancestor actor (partial instance? window/class)))
                                                                  (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (image/create (textures/texture-region textures (property/image property)))
                    (actor/add-listener! (text-tooltip/new (property/tooltip property) skin))
                    (actor/set-user-object! property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create "-" skin)
                  (actor/add-listener! (change-listener/create
                                           (fn [event _actor]
                                             (redo-rows (:stage/ctx (event/get-stage event))
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
                    (layout/pack (find-ancestor table (partial instance? window/class))))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (doto (text-button/create "+" skin)
                   (actor/add-listener! (change-listener/create
                                            (fn [event _actor]
                                              (let [{:keys [ctx/db
                                                            ctx/skin
                                                            ctx/stage
                                                            ctx/textures]
                                                     :as ctx} (:stage/ctx (event/get-stage event))]
                                                (stage/add-actor!
                                                 stage
                                                 (property-overview-window
                                                  {:db db
                                                   :textures textures
                                                   :skin skin
                                                   :property-type property-type
                                                   :clicked-id-fn (fn [actor id ctx]
                                                                    (actor/remove! (find-ancestor actor (partial instance? window/class)))
                                                                    (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (doto (image/create (textures/texture-region textures (property/image property)))
                     (actor/add-listener! (text-tooltip/new (property/tooltip property) skin))
                     (actor/set-user-object! property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create "-" skin)
                   (actor/add-listener! (change-listener/create
                                            (fn [event _actor]
                                              (redo-rows (:stage/ctx (event/get-stage event))
                                                         nil)))))})]])))

(defn- rebuild-editor-window!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.clojure.editor-window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (actor/remove! window)
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
                                     (doto (text-button/create "-" skin)
                                       (actor/add-listener! (change-listener/create
                                                                (fn [event _actor]
                                                                  (actor/remove! (first (filter (fn [actor]
                                                                                                          (and (actor/get-user-object actor)
                                                                                                               (= k ((actor/get-user-object actor) 0))))
                                                                                                        (group/get-children table))))
                                                                  (let [ctx (:stage/ctx (event/get-stage event))]
                                                                    (rebuild-editor-window! ctx)))))))
                            :left? true}
                           {:actor (label/create (k-label-text k) skin)}]]})
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
  (let [window (doto (doto (window/new "Choose" skin)
    (table/set-opts! {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}}))
                 (add-close-button! skin)
                 (window/setModal true))
        remaining-ks (sort (remove (set (keys (widget-value schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create (name k) skin)
                  (actor/add-listener! (change-listener/create
                                           (fn [event _actor]
                                             (actor/remove! window)
                                             (let [ctx (:stage/ctx (event/get-stage event))]
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
    (layout/pack window)
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
                (actor/set-name! "moon.db.schema.map.ui.widget"))
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
                [{:actor (doto (text-button/create "Add component" skin)
                           (actor/add-listener! (change-listener/create
                                                    (fn [event actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/stage
                                                                    ctx/skin]} (:stage/ctx (event/get-stage event))]
                                                        (stage/add-actor!
                                                         stage
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
  (label/create (string/truncate (binding [*print-level* nil]
                         (pr-str v))
                       60)
             skin))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create {:table/cell-defaults {:pad 1}
                :table/rows [(for [image (:animation/frames animation)]
                               {:actor
                                (let [scale 2
                                      texture-region (textures/texture-region textures image)]
                                  (image-button/new
                                   (doto (texture-region-drawable/new texture-region)
                                     (texture-region-drawable/setMinSize (* scale (texture-region/getRegionWidth texture-region))
                                                                       (* scale (texture-region/getRegionHeight texture-region))))))})]}))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (check-box/new "" skin)
    (check-box/setChecked checked?)))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (doto (select-box/new skin)
    (select-box/setItems (map pr-str (rest schema)))
    (select-box/setSelected (pr-str v))))

(defmethod create-widget :s/image
  [_ image {:keys [ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/setMinSize (* scale (texture-region/getRegionWidth texture-region))
                                              (* scale (texture-region/getRegionHeight texture-region)))))))

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
  (doto (text-field/new (pr-str v) skin)
    (actor/add-listener! (text-tooltip/new (str schema) skin))))

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
                             (doto (text-button/create "No sound" skin)
                               (actor/add-listener! (change-listener/create
                                                   (fn [event _actor]
                                                     ((open-select-fn table)
                                                      (:stage/ctx (event/get-stage event)))))))}])])
      table)))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (str v) skin)
    (actor/add-listener! (text-tooltip/new (str schema) skin))))

(defmethod create-widget :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (pr-str v) skin)
    (actor/add-listener! (text-tooltip/new (str schema) skin))))

(defn- main-window-f
  [{:keys [ctx/db
           ctx/skin]}]
  (doto (window/new "Edit" skin)
    (table/set-opts! {:title "Edit"
                               :skin skin
                               :table/rows (for [property-type (sort (db/property-types db))]
                                             [{:actor (doto (text-button/create (str/capitalize (name property-type)) skin)
                                                            (actor/add-listener! (change-listener/create
                                                                                (fn [event _actor]
                                                                                  (let [{:keys [ctx/db
                                                                                                ctx/skin
                                                                                                ctx/stage
                                                                                                ctx/textures]
                                                                                         :as ctx} (:stage/ctx (event/get-stage event))]
                                                                                    (stage/add-actor! stage
                                                                                                    (property-overview-window
                                                                                                     {:db db
                                                                                                      :textures textures
                                                                                                      :skin skin
                                                                                                      :property-type property-type
                                                                                                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                                                       (stage/add-actor! stage
                                                                                                                                       (property-editor-window
                                                                                                                                        {:ctx ctx
                                                                                                                                         :property (db/get-raw db id)})))})))))))}])})))

(defn- input-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (application/get-input app)))

(defn- audio-f [{:keys [ctx/app ctx/files] :as ctx}]
  (assoc ctx :ctx/audio (audio/create (application/get-audio app) files)))

(defn- files-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/get-files app)))

(defn- graphics-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/get-graphics app)))

(defn- batch-f [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(defn- skin-f [{:keys [ctx/files] :as ctx}]
  (let [skin (ui-skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (ui-skin/getFont "default-font")
        bitmap-font/getData
        (bitmap-font-data/set-markupEnabled true))
    (assoc ctx :ctx/skin skin)))

(defn- db-f [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn- stage-f [{:keys [ctx/input
                        ctx/batch] :as ctx}]
  (let [stage* (stage/create (fit-viewport/new 1440 900) batch)]
    (input/set-processor! input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/add-actor! (:ctx/stage ctx) (main-window-f ctx))
      ctx)))

(defn- textures-f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (textures/create files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})))

(defn create [application]
  (-> {:ctx/app application}
      input-f
      files-f
      audio-f
      graphics-f
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
  (disposable/dispose! batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures)))

(defn- clear-screen
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/getGL20 graphics)]
    (gl20/glClearColor gl 0 0 0 0)
    (gl20/glClear gl gl20/GL_COLOR_BUFFER_BIT))
  ctx)

(defn render [{:keys [ctx/stage]
               :as ctx}]
  (let [ctx (clear-screen ctx)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize [{:keys [ctx/stage]} width height]
  (viewport/update (:stage/viewport stage) width height true))

(defn -main []
  (config/use-glfw-async!)
  (lwjgl3-application/create
   {:application/listener {:create! (fn [application]
                                      (reset! state (create application)))
                           :dispose! (fn []
                                       (dispose @state))
                           :render! (fn []
                                      (swap! state render))
                           :resize! (fn [width height]
                                      (resize @state width height))
                           :pause! (fn [])
                           :resume! (fn [])}
    :application/config {:config/set-title "!Editor!"
                         :config/set-windowed-mode {:width 1440
                                                    :height 900}
                         :config/set-foreground-fps 60}}))
