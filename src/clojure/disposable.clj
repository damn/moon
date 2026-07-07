(ns clojure.disposable
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose! [disposable]
  (Disposable/.dispose disposable))
