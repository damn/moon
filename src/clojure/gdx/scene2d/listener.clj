(ns clojure.gdx.scene2d.listener
  (:require [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]))

(defmulti create
  (fn [[listener-k listener-params]]
    listener-k))

(defmethod create
  :listener/text-tooltip
  [[_ [tooltip skin]]]
  (text-tooltip/create tooltip skin))

(defmethod create
  :listener/change
  [[_ f]]
  (change-listener/create f))

(defmethod create
  :listener/click
  [[_ f]]
  (click-listener/create f))
