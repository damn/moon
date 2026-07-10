(ns gdl.disposable
  (:require [com.badlogic.gdx.utils.disposable :as disposable]))

(defn dispose! [disposable]
  (disposable/dispose disposable))
