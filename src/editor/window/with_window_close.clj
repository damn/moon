(ns editor.window.with-window-close
  (:require [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [gdx.stage :as stage]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [gdx.scenes.scene2d.ui :as ui]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (.getStage actor)]
       (stage/set-ctx! stage new-ctx))
     (.remove (find-ancestor actor ui/window?))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! stage
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))
