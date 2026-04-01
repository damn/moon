(ns moon.schema.boolean
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn malli-form [_ _schemas]
  :boolean)

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (assert (boolean? checked?))
  (doto (check-box/create "" skin)
    (check-box/set-checked! checked?)))

(defn value
  [_ widget _schemas]
  (check-box/checked? widget))
