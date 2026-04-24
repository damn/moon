(ns moon.schema.boolean
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.check-box :as check-box]))

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/check-box
    :skin skin
    :checked? checked?}))

(defn value
  [_ widget _schemas]
  (check-box/checked? widget))
