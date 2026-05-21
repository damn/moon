(ns game.schema-widget.boolean
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.check-box :as check-box]
            [moon.schema :as schema]))

(defmethod schema/create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/check-box
    :skin skin
    :checked? checked?}))

(defmethod schema/value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))
