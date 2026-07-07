(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn post-runnable! [application f]
  (Application/.postRunnable application f))
