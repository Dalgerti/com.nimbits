package com.nimbits.io.command;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;

import java.util.List;

public class CreateCommand extends AbstractCommand implements Command {

    private final static String USAGE = "create a new entity: create <entity type> <entity name> e.g create point foobar";

    public CreateCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {

        if (args.length != 3) {
            System.out.println(USAGE);
        }
        else {
            try {
                String type = args[1];
                String name = args[2];
                EntityType entityType = EntityType.valueOf(type);
                if (entityType.equals(EntityType.point)) {
                    EntityHelper helper = HelperFactory.getEntityHelper(server);
                    Point point = helper.createPoint(name, entityType,  current);
                    tree.add(point);
                    listener.onTreeUpdated(tree);
                    listener.setCurrent(current);

                }
                else {
                    listener.onMessage("you can only create a type point for now.");
                }
            }
            catch (Exception ex) {
                listener.onMessage(ex.getMessage());
            }

        }


    }



    @Override
    public String getUsage() {
        return USAGE;
    }
}
