(ns clojure.build-widget
  (:require
            [clojure.set-user-object]
            [clojure.create-widget :as create-widget]))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (clojure.set-user-object/f widget [k v])
    widget))
