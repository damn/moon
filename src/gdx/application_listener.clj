(ns gdx.application-listener
  (:require [com.badlogic.gdx.application-listener :as application-listener]))

(defn create [listener-spec]
  (application-listener/new listener-spec))
