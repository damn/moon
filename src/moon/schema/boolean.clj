(ns moon.schema.boolean
  (:require [clojure.scene2d.ui.check-box :as check-box]
            [moon.ui :as ui]))

(defn malli-form [_ _schemas]
  :boolean)

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (ui/create
   {:type :ui/check-box
    :skin skin
    :checked? checked?}))

(defn value
  [_ widget _schemas]
  (check-box/checked? widget))
