(ns clojure.gdx
  "Facade for `com.badlogic.gdx.Gdx` class - we should only reference this class once in our project, so we know what we are using from libgdx and can rewrite it later if required."
  (:import (com.badlogic.gdx Gdx)))

(defn app []
  Gdx/app)
