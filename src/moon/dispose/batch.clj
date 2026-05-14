(ns moon.dispose.batch
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/batch]}]
  (Disposable/.dispose batch))
