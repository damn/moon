(ns moon.ui.error-window
  (:require [clojure.repl]
            [moon.ui :as ui]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [{:keys [skin throwable]}]
  (ui/actor
   {:type :ui/window
    :title "Error"
    :rows [[{:actor (Label. (binding [*print-level* 3]
                              (utils/with-err-str
                                (clojure.repl/pst throwable)))
                            ^Skin skin)}]]
    :modal? true
    :close-button? true
    :close-on-escape? true
    :center? true
    :skin skin
    :pack? true}))
