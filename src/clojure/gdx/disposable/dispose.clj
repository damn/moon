(ns clojure.gdx.disposable.dispose
  (:import (com.badlogic.gdx.utils Disposable)))

(defn f [disposable]
  (Disposable/.dispose disposable))
