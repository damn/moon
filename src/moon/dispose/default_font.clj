(ns moon.dispose.default-font
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/default-font]}]
  (Disposable/.dispose default-font))
