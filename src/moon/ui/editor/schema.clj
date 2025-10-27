(ns moon.ui.editor.schema
  (:require [clojure.edn :as edn]
            [clojure.edn :as edn]
            [moon.audio :as audio]
            [moon.db :as db]
            [moon.graphics :as graphics]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.group :as group]
            [moon.scene2d.ui.widget-group :as widget-group]
            [moon.ui :as ui]
            [moon.ui.check-box :as check-box]
            [moon.ui.editor.property :as property]
            [moon.ui.image :as image]
            [moon.ui.image-button :as image-button]
            [moon.ui.label :as label]
            [moon.ui.select-box :as select-box]
            [moon.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.text-field :as text-field]
            [moon.ui.tooltip :as tooltip]
            [moon.ui.window :as window]
            [moon.utils :as utils]))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod create :default
  [_ v _ctx]
  (label/create (utils/truncate (utils/->edn-str v) 60)))

(defmethod value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))

(defmethod create :s/animation
  [_ animation {:keys [ctx/graphics]}]
  (table/create
   {:rows [(for [image (:animation/frames animation)]
             {:actor (image-button/create
                      {:drawable/texture-region (graphics/texture-region graphics image)
                       :drawable/scale 2})})]
    :cell-defaults {:pad 1}}))

(defmethod create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (assert (boolean? checked?))
  (check-box/create
   :text ""
   :on-clicked (fn [_])
   :checked? checked?
   :skin skin
   ))

(defmethod value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

(defmethod create :s/enum [schema v _ctx]
  (select-box/create
   {:items (map utils/->edn-str (rest schema))
    :selected (utils/->edn-str v)}))

(defmethod value :s/enum [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))

(defmethod create :s/image
  [schema  image {:keys [ctx/graphics]}]
  (image-button/create
   {:drawable/texture-region (graphics/texture-region graphics image)
    :drawable/scale 2})
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here


; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (texture/region (assets file))})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/graphics]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (widget-group/pack! (window/find-ancestor table)))]
    (table/add-rows!
     table
     [[{:actor (text-button/create
                {:text "+"
                 :on-clicked (fn [_actor {:keys [ctx/db
                                                 ctx/graphics
                                                 ctx/stage]}]
                               (stage/add-actor!
                                stage
                                {:type :actor/editor-overview-window
                                 :db db
                                 :graphics graphics
                                 :property-type property-type
                                 :clicked-id-fn (fn [actor id ctx]
                                                  (actor/remove! (window/find-ancestor actor))
                                                  (redo-rows ctx (conj property-ids id)))}))})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)
              texture-region (graphics/texture-region graphics (property/image property))
              image-widget (image/create
                            {:image/object texture-region
                             :actor/user-object property-id})]
          {:actor (tooltip/add! image-widget (property/tooltip property))}))
      (for [id property-ids]
        {:actor (text-button/create
                 {:text "-"
                  :on-clicked (fn [_actor ctx]
                                (redo-rows ctx (disj property-ids id)))})})])))

(defmethod create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod value :s/one-to-many [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/graphics]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (widget-group/pack! (window/find-ancestor table)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (text-button/create
                  {:text "+"
                   :on-clicked (fn [_actor {:keys [ctx/db
                                                   ctx/graphics
                                                   ctx/stage]}]
                                 (stage/add-actor!
                                  stage
                                  {:type :actor/editor-overview-window
                                   :db db
                                   :graphics graphics
                                   :property-type property-type
                                   :clicked-id-fn (fn [actor id ctx]
                                                    (actor/remove! (window/find-ancestor actor))
                                                    (redo-rows ctx id))}))})})]
      [(when property-id
         (let [property (db/get-raw db property-id)
               texture-region (graphics/texture-region graphics (property/image property))
               image-widget (image/create
                             {:image/object texture-region
                              :actor/user-object property-id})]
           {:actor (tooltip/add! image-widget (property/tooltip property))}
           image-widget))]
      [(when property-id
         {:actor (text-button/create
                  {:text "-"
                   :on-clicked (fn [_actor ctx]
                                 (redo-rows ctx nil))})})]])))

(defmethod create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (table/create
               {:cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod value :s/one-to-one [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor _ctx]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns table sound-name)])
    (actor/remove! (window/find-ancestor actor))
    (widget-group/pack! (window/find-ancestor table))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [_actor {:keys [ctx/audio
                      ctx/stage]}]
    (stage/add-actor! stage
                      {:type :actor/scroll-pane-window
                       :viewport-height (ui/viewport-width stage)
                       :rows (for [sound-name (audio/sound-names audio)]
                               [{:actor (text-button/create
                                         {:text sound-name
                                          :on-clicked (rebuild-sound-widget! table sound-name)})}
                                {:actor (text-button/create
                                         {:text "play!"
                                          :on-clicked (fn [_actor {:keys [ctx/audio]}]
                                                        (audio/play! audio sound-name))})}])})))

(defn- sound-columns [table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :on-clicked (open-select-sounds-handler table)})}
   {:actor (text-button/create
            {:text "play!"
             :on-clicked (fn [_actor {:keys [ctx/audio]}]
                           (audio/play! audio sound-name))})}])

(defmethod create :s/sound [_  sound-name _ctx]
  (let [table (table/create {:cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns table sound-name)
                              [{:actor (text-button/create
                                        {:text "No sound"
                                         :on-clicked (open-select-sounds-handler table)})}])])
    table))

(defmethod create :s/string [schema v _ctx]
  (tooltip/add! (text-field/create (str v))
                (str schema)))

(defmethod value :s/string [_ widget _schemas]
  (text-field/text widget))

(defn- create-edn-widget [schema v _ctx]
  (tooltip/add! (text-field/create (utils/->edn-str v))
                (str schema)))

(defn- edn-widget-value [_  widget _schemas]
  (edn/read-string (text-field/text widget)))

(def fn-map
  {:s/number {create  create-edn-widget
              value   edn-widget-value}
   :s/val-max {create create-edn-widget
               value  edn-widget-value}})

(doseq [[schema-k impls] fn-map
        [multifn method-var] impls]
  (clojure.lang.MultiFn/.addMethod multifn schema-k method-var))
