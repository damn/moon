(ns clojure.create-widget
  (:require [clojure.add-listener]
            [clojure.add-one-to-many-rows :refer [add-one-to-many-rows]]
            [clojure.add-one-to-one-rows :refer [add-one-to-one-rows]]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.animation-image-button :as animation-image-button]
            [clojure.checkbox :as checkbox]
            [clojure.columns :refer [sound-columns]]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.event :as event]
            [clojure.image-button :as image-button]
            [clojure.moon-textures :as textures]
            [clojure.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [clojure.optional :refer [optional?]]
            [clojure.schemas-optional-keyset :refer [optional-keyset]]
            [clojure.set :as set]
            [clojure.set-user-object]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.truncate :refer [truncate]]
            [clojure.ui-label :as label]
            [clojure.ui-select-box :as select-box]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.utils-change-listener :as change-listener]))

(defmulti f
  (fn [[schema-k :as _schema] v ctx]
    schema-k))

(defn build-widget [ctx schema k v]
  (let [widget (f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (clojure.set-user-object/f widget [k v])
    widget))

(defmethod f :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod f :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (animation-image-button/f textures image 2)})]}))

(defmethod f :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (checkbox/new "" skin)
    (checkbox/set-checked! checked?)))

(defmethod f :s/enum
  [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defmethod f :s/image
  [_ image {:keys [ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                       (* scale (texture-region/get-region-height texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here

(defmethod f :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin
           ctx/create-map-widget-table
           ctx/property-k-sort-order
           ctx/create-component-row]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (create-map-widget-table
     {:create-component-row create-component-row
      :skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod f :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))

(defmethod f :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod f :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod f :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (clojure.add-listener/f (change-listener/create
                                             (fn [event _actor]
                                               ((open-select-sounds-handler table)
                                                (:stage/ctx (event/get-stage event))
                                                sound-columns)))))}])])
    table))

(defmethod f :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))

(defmethod f :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))
