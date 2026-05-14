(ns moon.dispose.audio
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/audio]}]
  (run! Disposable/.dispose (vals audio)))
