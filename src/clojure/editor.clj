(ns clojure.editor
  (:require [clojure.ctx :as ctx]
            [clojure.db :as db]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-add-one-to-many-rows :as add-one-to-many-rows]
            [clojure.editor.create-widget-add-one-to-one-rows :as add-one-to-one-rows]
            [clojure.editor.create-widget-build-widget :as build-widget]
            [clojure.editor.create-widget-map-widget-table-create :as map-widget-table-create]
            [clojure.editor.create-widget-open-select-sounds-handler :as open-select-sounds-handler]
            [clojure.editor.create-widget-property-editor-window :refer [property-editor-window]]
            [clojure.editor.create-widget-sound-columns :as sound-columns]
            [clojure.editor.property-k-sort-order :refer [property-k-sort-order]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [clojure.edn.v-to-str :refer [->edn-str]]
            [clojure.files.create-textures :as create-textures]
            [clojure.malli-form-register-methods]
            [clojure.moon-textures :as textures]
            [clojure.optional :refer [optional?]]
            [clojure.property-types :refer [property-types]]
            [clojure.scene2d-stage :as scene2d-stage]
            [clojure.schemas-optional-keyset :refer [optional-keyset]]
            [clojure.set :as set]
            [clojure.set-ctx :as set-ctx]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.string :as str]
            [clojure.table-set-opts :as table-set-opts]
            [clojure.truncate :refer [truncate]]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as ui-skin]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(def state (atom nil))

(defmethod create-widget :default
  [_ v {:keys [ctx/skin]}]
  (label/new (truncate (->edn-str v) 60) skin))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 1}
                               :table/rows [(for [image (:animation/frames animation)]
                                              {:actor
                                               (let [scale 2
                                                     texture-region (textures/texture-region textures image)]
                                                 (image-button/new
                                                  (doto (texture-region-drawable/new texture-region)
                                                    (texture-region-drawable/setMinSize (* scale (texture-region/getRegionWidth texture-region))
                                                                                       (* scale (texture-region/getRegionHeight texture-region))))))})]})))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (check-box/new "" skin)
    (check-box/setChecked checked?)))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (doto (select-box/new skin)
    (select-box/setItems (map ->edn-str (rest schema)))
    (select-box/setSelected (->edn-str v))))

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
    (map-widget-table-create/map-widget-table-create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget/build-widget ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod create-widget :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (->edn-str v) skin)
    (actor/addListener (text-tooltip/new (str schema) skin))))

(defmethod create-widget :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (doto (table/new)
                (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (add-one-to-many-rows/add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod create-widget :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (doto (table/new)
                (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (add-one-to-one-rows/add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod create-widget :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (doto (table/new)
                (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (letfn [(sound-columns-fn [skin table sound-name]
              (sound-columns/sound-columns skin table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler/open-select-sounds-handler table sound-columns-fn))]
      (add-rows! table [(if sound-name
                           (sound-columns-fn skin table sound-name)
                           [{:actor
                             (doto (text-button/new "No sound" skin)
                               (actor/addListener (change-listener/create
                                                   (fn [event _actor]
                                                     ((open-select-fn table)
                                                      (:stage/ctx (event/getStage event)))))))}])])
      table)))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (str v) skin)
    (actor/addListener (text-tooltip/new (str schema) skin))))

(defmethod create-widget :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (->edn-str v) skin)
    (actor/addListener (text-tooltip/new (str schema) skin))))

(defn- main-window-f
  [{:keys [ctx/db
           ctx/skin]}]
  (doto (window/new "Edit" skin)
    (table-set-opts/set-opts! {:title "Edit"
                               :skin skin
                               :table/rows (for [property-type (sort (property-types db))]
                                             [{:actor (doto (text-button/new (str/capitalize (name property-type)) skin)
                                                            (actor/addListener (change-listener/create
                                                                                (fn [event _actor]
                                                                                  (let [{:keys [ctx/db
                                                                                                ctx/skin
                                                                                                ctx/stage
                                                                                                ctx/textures]
                                                                                         :as ctx} (:stage/ctx (event/getStage event))]
                                                                                    (stage/addActor stage
                                                                                                    (property-overview-window
                                                                                                     {:db db
                                                                                                      :textures textures
                                                                                                      :skin skin
                                                                                                      :property-type property-type
                                                                                                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                                                       (stage/addActor stage
                                                                                                                                       (property-editor-window
                                                                                                                                        {:ctx ctx
                                                                                                                                         :property (db/get-raw db id)})))})))))))}])})))

(defn- input-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (application/getInput app)))

(defn- audio-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (application/getAudio app)))

(defn- files-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/getFiles app)))

(defn- graphics-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/getGraphics app)))

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
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/addActor (:ctx/stage ctx) (main-window-f ctx))
      ctx)))

(defn- textures-f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (create-textures/f files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}})))

(defn create [application]
  (-> {:ctx/app application}
      input-f
      audio-f
      files-f
      graphics-f
      batch-f
      skin-f
      db-f
      stage-f
      textures-f))

(defn dispose [{:keys [ctx/skin
                       ctx/batch
                       ctx/textures]}]
  (disposable/dispose batch)
  (disposable/dispose skin)
  (run! disposable/dispose (vals textures)))

(defn render [{:keys [ctx/stage]
               :as ctx}]
  (let [ctx (ctx/clear ctx)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act stage)
    (stage/draw stage)
    (:stage/ctx stage)))

(defn resize [{:keys [ctx/stage]} width height]
  (viewport/update (:stage/viewport stage) width height true))

(defn -main []
  (lwjgl3-application/create {:create! (fn [app]
                                         (reset! state (create app)))
                              :dispose! (fn []
                                          (dispose @state))
                              :render! (fn []
                                         (swap! state render))
                              :resize! (fn [width height]
                                         (resize @state width height))}
                             {:config/set-title "!Editor!"
                              :config/set-windowed-mode {:width 1440
                                                         :height 900}
                              :config/set-foreground-fps 60}))
