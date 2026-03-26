(ns moon.actor)

(defprotocol Actor
  (set-position! [_ [x y]])
  (set-visible! [_ visible?])
  (visible? [_])
  (parent [_]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn find-ancestor
  [actor clazz]
  (if-let [parent (parent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
