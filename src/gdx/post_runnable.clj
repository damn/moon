(ns gdx.post-runnable
  (:import (com.badlogic.gdx Gdx)))

(defn post-runnable! [f]
  (.postRunnable Gdx/app f))
