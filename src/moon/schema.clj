(ns moon.schema
  (:require clojure.edn
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.check-box :as check-box]
            [clojure.scene2d.ui.select-box :as select-box]
            [moon.edn :as edn]
            [moon.string :as string]
            [moon.textures :as textures]
            ))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod create :default
  [_ v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/label
    :text (string/truncate (edn/->str v) 60)
    :skin skin}))

(defmethod value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))

(defmethod create :s/animation
  [_ animation {:keys [ctx/skin
                       ctx/textures]}]
  (actor/create
   {:type :ui/table
    :table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (actor/create
                            {:type :ui/image-button
                             :drawable {:drawable/texture-region (textures/texture-region textures image)
                                        :drawable/scale 2}})})]}))

(defmethod create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/check-box
    :skin skin
    :checked? checked?}))

(defmethod value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

(defmethod create :s/enum [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/select-box
    :skin skin
    :items (map edn/->str (rest schema))
    :selected (edn/->str v)}))

(defmethod value :s/enum [_  widget _schemas]
  (clojure.edn/read-string (select-box/selected widget)))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (texture/region (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defmethod create :s/image
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (actor/create
   {:type :ui/image-button
    :drawable {:drawable/texture-region (textures/texture-region textures image)
               :drawable/scale 2}})
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
