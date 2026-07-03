(ns clojure.gdx.application.post-runnable!
  (:import (com.badlogic.gdx Application)))

(defn f [application f]
  (Application/.postRunnable application f))
