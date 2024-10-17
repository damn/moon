(ns editor.widgets
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [component.db :as db]
            [component.info :as info]
            [component.property :as property]
            [editor.widget :as widget]
            [editor.overview :refer [overview-table]]
            [editor.utils :refer [scrollable-choose-window]]
            [gdx.graphics :as g]
            [gdx.assets :as assets]
            [gdx.ui :as ui]
            [gdx.ui.actor :as a]
            [gdx.ui.stage-screen :refer [stage-add!]]
            [utils.core :refer [truncate ->edn-str]]))

(defn- add-schema-tooltip! [widget schema]
  (ui/add-tooltip! widget (str schema))
  widget)

;;

(defmethod widget/create :default [_ v]
  (ui/label (truncate (->edn-str v) 60)))

(defmethod widget/value :default [_ widget]
  (a/id widget))

;;

(defmethod widget/create :boolean [_ checked?]
  (assert (boolean? checked?))
  (ui/check-box "" (fn [_]) checked?))

(defmethod widget/value :boolean [_ widget]
  (.isChecked ^com.kotcrab.vis.ui.widget.VisCheckBox widget))

;;

(defmethod widget/create :string [schema v]
  (add-schema-tooltip! (ui/text-field v {}) schema))

(defmethod widget/value :string [_ widget]
  (.getText ^com.kotcrab.vis.ui.widget.VisTextField widget))

;;

(defmethod widget/create number? [schema v]
  (add-schema-tooltip! (ui/text-field (->edn-str v) {}) schema))

(defmethod widget/value number? [_ widget]
  (edn/read-string (.getText ^com.kotcrab.vis.ui.widget.VisTextField widget)))

;;

(defmethod widget/create :enum [schema v]
  (ui/select-box {:items (map ->edn-str (rest schema))
                  :selected (->edn-str v)}))

(defmethod widget/value :enum [_ widget]
  (edn/read-string (.getSelected ^com.kotcrab.vis.ui.widget.VisSelectBox widget)))

;;

(defmethod db/edn->value :s/image [_ image]
  (g/edn->image image))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
(defn- texture-rows []
  (for [file (sort assets/all-texture-files)]
    [(ui/image-button (g/image file) (fn []))]
    #_[(ui/text-button file (fn []))]))

(defmethod widget/create :s/image [_ image]
  (ui/image->widget (g/edn->image image) {})
  #_(ui/image-button image
                     #(stage-add! (scrollable-choose-window (texture-rows)))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here

;;

(defmethod widget/create :s/animation [_ animation]
  (ui/table {:rows [(for [image (:frames animation)]
                      (ui/image->widget (g/edn->image image) {}))]
             :cell-defaults {:pad 1}}))

;;


(defn- ->play-sound-button [sound-file]
  (ui/text-button "play!" #(assets/play-sound! sound-file)))

(declare ->sound-columns)

(defn- open-sounds-window! [table]
  (let [rows (for [sound-file assets/all-sound-files]
               [(ui/text-button (str/replace-first sound-file "sounds/" "")
                                (fn []
                                  (ui/clear-children! table)
                                  (ui/add-rows! table [(->sound-columns table sound-file)])
                                  (a/remove! (ui/find-ancestor-window ui/*on-clicked-actor*))
                                  (ui/pack-ancestor-window! table)
                                  (a/set-id! table sound-file)))
                (->play-sound-button sound-file)])]
    (stage-add! (scrollable-choose-window rows))))

(defn- ->sound-columns [table sound-file]
  [(ui/text-button (name sound-file) #(open-sounds-window! table))
   (->play-sound-button sound-file)])

(defmethod widget/create :s/sound [_ sound-file]
  (let [table (ui/table {:cell-defaults {:pad 5}})]
    (ui/add-rows! table [(if sound-file
                           (->sound-columns table sound-file)
                           [(ui/text-button "No sound" #(open-sounds-window! table))])])
    table))

;;

; TODO schemas not checking if that property exists in db...
; https://github.com/damn/core/issues/59

(defn- add-one-to-many-rows [table property-type property-ids]
  (let [redo-rows (fn [property-ids]
                    (ui/clear-children! table)
                    (add-one-to-many-rows table property-type property-ids)
                    (ui/pack-ancestor-window! table))]
    (ui/add-rows!
     table
     [[(ui/text-button "+"
                       (fn []
                         (let [window (ui/window {:title "Choose"
                                                  :modal? true
                                                  :close-button? true
                                                  :center? true
                                                  :close-on-escape? true})
                               clicked-id-fn (fn [id]
                                               (a/remove! window)
                                               (redo-rows (conj property-ids id)))]
                           (.add window (overview-table property-type clicked-id-fn))
                           (.pack window)
                           (stage-add! window))))]
      (for [property-id property-ids]
        (let [property (db/get property-id)
              image-widget (ui/image->widget (property/->image property) {:id property-id})]
          (ui/add-tooltip! image-widget #(info/->text property))
          image-widget))
      (for [id property-ids]
        (ui/text-button "-" #(redo-rows (disj property-ids id))))])))

(defmethod widget/create :s/one-to-many [[_ property-type] property-ids]
  (let [table (ui/table {:cell-defaults {:pad 5}})]
    (add-one-to-many-rows table property-type property-ids)
    table))

(defmethod widget/value :s/one-to-many [_ widget]
  (->> (ui/children widget)
       (keep a/id)
       set))

(defn- add-one-to-one-rows [table property-type property-id]
  (let [redo-rows (fn [id]
                    (ui/clear-children! table)
                    (add-one-to-one-rows table property-type id)
                    (ui/pack-ancestor-window! table))]
    (ui/add-rows!
     table
     [[(when-not property-id
         (ui/text-button "+"
                         (fn []
                           (let [window (ui/window {:title "Choose"
                                                    :modal? true
                                                    :close-button? true
                                                    :center? true
                                                    :close-on-escape? true})
                                 clicked-id-fn (fn [id]
                                                 (a/remove! window)
                                                 (redo-rows id))]
                             (.add window (overview-table property-type clicked-id-fn))
                             (.pack window)
                             (stage-add! window)))))]
      [(when property-id
         (let [property (db/get property-id)
               image-widget (ui/image->widget (property/->image property) {:id property-id})]
           (ui/add-tooltip! image-widget #(info/->text property))
           image-widget))]
      [(when property-id
         (ui/text-button "-" #(redo-rows nil)))]])))

(defmethod widget/create :s/one-to-one [[_ property-type] property-id]
  (let [table (ui/table {:cell-defaults {:pad 5}})]
    (add-one-to-one-rows table property-type property-id)
    table))

(defmethod widget/value :s/one-to-one [_ widget]
  (->> (ui/children widget) (keep a/id) first))