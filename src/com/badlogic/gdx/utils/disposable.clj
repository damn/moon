(ns com.badlogic.gdx.utils.disposable
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose [disposable]
  (Disposable/.dispose disposable))
