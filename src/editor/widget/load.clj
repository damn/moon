(ns editor.widget.load
  (:require [clojure.core-ext :refer [->edn-str
                                      truncate]]
            [clojure.edn :as edn]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [game.ctx :as ctx]
            [editor.widget :as widget]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.text-field :as text-field]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdx.stage :as stage]
            [moon.textures :as textures]
            [moon.ui.error-window]
            [editor.property-overview-window]))

(defmethod widget/create :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod widget/value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))

(defmethod widget/create :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (image-button/create
                            {:drawable (texture-region-drawable/create*
                                        {:drawable/texture-region (textures/texture-region textures image)
                                         :drawable/scale 2})})})]}))

(defmethod widget/create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))

(defmethod widget/value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

(defmethod widget/create :s/enum [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defmethod widget/value :s/enum [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (texture/region (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defmethod widget/create :s/image
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (image-button/create
   {:drawable (texture-region-drawable/create* {:drawable/texture-region (textures/texture-region textures image)
                                                :drawable/scale 2})})
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here

(defmethod widget/create :s/number
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/number
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor ui/window?))
    (widget-group/pack! (actor/find-ancestor table ui/window?))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (window/create
                       {:title "Choose"
                        :skin skin
                        :window/close-button? skin
                        :window/modal? true
                        :table/rows
                        [[(let [table (table/create
                                       {:table/cell-defaults {:pad 5}
                                        :table/rows (for [sound-name (ctx/sound-names ctx)]
                                                      [{:actor (text-button/create
                                                                {:text sound-name
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change
                                                                                   (fn [event actor]
                                                                                     ((rebuild-sound-widget! table sound-name) actor (:stage/ctx (event/stage event))))}})}
                                                       {:actor (text-button/create
                                                                {:text "play!"
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change (fn [event _actor]
                                                                                                      (ctx/do! (:stage/ctx (event/stage event))
                                                                                                               [[:tx/sound sound-name]]))}})}])} )]
                            {:actor (scroll-pane/create
                                     {:actor table
                                      :skin skin})
                             :width  (+ (actor/width table) 50)
                             :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                          (actor/height table))})]]}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}
   {:actor (text-button/create
            {:text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (ctx/do! (:stage/ctx (event/stage event))
                                                           [[:tx/sound sound-name]]))}})}])

(defmethod widget/create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor
                                (text-button/create
                                 {:text "No sound"
                                  :skin skin
                                  :actor/listeners {:listener/change
                                                    (fn [event _actor]
                                                      ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}])])
    table))

(defmethod widget/create :s/string [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/string [_ widget _schemas]
  (text-field/text widget))

(defmethod widget/create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/val-max
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
