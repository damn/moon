(ns moon.ui.actor
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor]))

#_(def ^:private opts
  [[:actor/listener actor/add-listener!]]
  )

(defn set-opts!
  [actor opts]
  (when-let [listener (:actor/listener opts)]
    (actor/add-listener! actor listener)))
